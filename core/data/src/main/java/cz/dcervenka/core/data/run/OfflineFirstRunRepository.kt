package cz.dcervenka.core.data.run

import cz.dcervenka.core.data.networking.get
import cz.dcervenka.core.database.dao.RunPendingSyncDao
import cz.dcervenka.core.database.mappers.toRun
import cz.dcervenka.core.domain.SessionStorage
import cz.dcervenka.core.domain.run.LocalRunDataSource
import cz.dcervenka.core.domain.run.RemoteRunDataSource
import cz.dcervenka.core.domain.run.Run
import cz.dcervenka.core.domain.run.RunId
import cz.dcervenka.core.domain.run.RunRepository
import cz.dcervenka.core.domain.run.SyncRunScheduler
import cz.dcervenka.core.domain.util.DataError
import cz.dcervenka.core.domain.util.EmptyResult
import cz.dcervenka.core.domain.util.Result
import cz.dcervenka.core.domain.util.asEmptyResult
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.plugin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class OfflineFirstRunRepository(
    private val localRunDataSource: LocalRunDataSource,
    private val remoteRunDataSource: RemoteRunDataSource,
    private val applicationScope: CoroutineScope,
    private val runPendingSyncDao: RunPendingSyncDao,
    private val sessionStorage: SessionStorage,
    private val syncRunScheduler: SyncRunScheduler,
    private val client: HttpClient,
) : RunRepository {

    override fun getRuns(): Flow<List<Run>> {
        return localRunDataSource.getRuns()
    }

    override suspend fun fetchRuns(): EmptyResult<DataError> {
        return when (val result = remoteRunDataSource.getRuns()) {
            is Result.Error -> result.asEmptyResult()
            is Result.Success -> {
                applicationScope.async {
                    localRunDataSource.upsertRuns(result.data).asEmptyResult()
                }.await()
            }
        }
    }

    override suspend fun upsertRun(run: Run, mapPicture: ByteArray): EmptyResult<DataError> {
        val localResult = localRunDataSource.upsertRun(run)
        // here it works with steps but the remote sync rewrites this result
        // change BE config to fix this

        if (localResult !is Result.Success) {
            return localResult.asEmptyResult()
        }

        val runWithId = run.copy(id = localResult.data)
        val remoteResult = remoteRunDataSource.postRun(
            run = runWithId,
            mapPicture = mapPicture
        )

        return when (remoteResult) {
            is Result.Error -> {
                applicationScope.launch {
                    syncRunScheduler.scheduleSync(
                        type = SyncRunScheduler.SyncType.CreateRun(
                            run = runWithId,
                            mapPictureBytes = mapPicture
                        )
                    )
                }.join()
                Result.Success(Unit)
            }

            is Result.Success -> {
                applicationScope.async {
                    // data.copy is a temp solution to see the steps at least after the run was finished
                    localRunDataSource.upsertRun(remoteResult.data.copy(steps = runWithId.steps)).asEmptyResult()
                }.await()
            }
        }
    }

    override suspend fun deleteRun(id: RunId) {
        localRunDataSource.deleteRun(id)

        // Edge case where the run is created in offline-mode, and then deleted in offline-mode
        // as well. In that case, we don't need to sync anything.
        val isPendingSync = runPendingSyncDao.getRunPendingSyncEntity(id) != null
        if (isPendingSync) {
            runPendingSyncDao.deleteRunPendingSyncEntity(id)
            return
        }

        val remoteResult = applicationScope.async {
            remoteRunDataSource.deleteRun(id)
        }.await()

        if (remoteResult is Result.Error) {
            applicationScope.launch {
                syncRunScheduler.scheduleSync(
                    type = SyncRunScheduler.SyncType.DeleteRun(id)
                )
            }.join()
        }
    }

    override suspend fun syncPendingRuns() {
        withContext(Dispatchers.IO) {
            val userId = sessionStorage.get()?.userId ?: return@withContext

            val createdRuns = async {
                runPendingSyncDao.getAllRunPendingSyncEntities(userId)
            }
            val deletedRuns = async {
                runPendingSyncDao.getAllDeletedRunSyncEntities(userId)
            }

            val createJobs = createdRuns
                .await()
                .map {
                    launch {
                        val run = it.run.toRun()
                        when (remoteRunDataSource.postRun(run, it.mapPictureBytes)) {
                            is Result.Error -> Unit
                            is Result.Success -> {
                                applicationScope.launch {
                                    runPendingSyncDao.deleteRunPendingSyncEntity(it.runId)
                                }.join()
                            }
                        }
                    }
                }
            val deleteJobs = deletedRuns
                .await()
                .map {
                    launch {
                        when (remoteRunDataSource.deleteRun(it.runId)) {
                            is Result.Error -> Unit
                            is Result.Success -> {
                                applicationScope.launch {
                                    runPendingSyncDao.deleteDeletedRunSyncEntity(it.runId)
                                }.join()
                            }
                        }
                    }
                }

            createJobs.forEach { it.join() }
            deleteJobs.forEach { it.join() }
        }
    }

    override suspend fun deleteAllRuns() {
        localRunDataSource.deleteRuns()
    }

    override suspend fun logout(): EmptyResult<DataError.Network> {
        val result = client.get<Unit>(
            route = "/logout"
        ).asEmptyResult()

        client.plugin(Auth).providers.filterIsInstance<BearerAuthProvider>()
            .firstOrNull()?.clearToken()

        return result
    }
}