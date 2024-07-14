package cz.dcervenka.core.data.run

import cz.dcervenka.core.domain.run.LocalRunDataSource
import cz.dcervenka.core.domain.run.RemoteRunDataSource
import cz.dcervenka.core.domain.run.Run
import cz.dcervenka.core.domain.run.RunId
import cz.dcervenka.core.domain.run.RunRepository
import cz.dcervenka.core.domain.util.DataError
import cz.dcervenka.core.domain.util.EmptyResult
import cz.dcervenka.core.domain.util.Result
import cz.dcervenka.core.domain.util.asEmptyResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow


class OfflineFirstRunRepository(
    private val localRunDataSource: LocalRunDataSource,
    private val remoteRunDataSource: RemoteRunDataSource,
    private val applicationScope: CoroutineScope,
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
                // TODO handle this later
                Result.Success(Unit)
            }

            is Result.Success -> {
                applicationScope.async {
                    localRunDataSource.upsertRun(remoteResult.data).asEmptyResult()
                }.await()
            }
        }
    }

    override suspend fun deleteRun(id: RunId) {
        localRunDataSource.deleteRun(id)

        val remoteResult = applicationScope.async {
            remoteRunDataSource.deleteRun(id)
        }.await()
    }
}