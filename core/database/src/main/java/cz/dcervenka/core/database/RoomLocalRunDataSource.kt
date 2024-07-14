package cz.dcervenka.core.database

import android.database.sqlite.SQLiteFullException
import cz.dcervenka.core.database.dao.RunDao
import cz.dcervenka.core.database.mappers.toRun
import cz.dcervenka.core.database.mappers.toRunEntity
import cz.dcervenka.core.domain.run.LocalRunDataSource
import cz.dcervenka.core.domain.run.Run
import cz.dcervenka.core.domain.run.RunId
import cz.dcervenka.core.domain.util.DataError
import cz.dcervenka.core.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomLocalRunDataSource(
    private val runDao: RunDao
) : LocalRunDataSource {
    override fun getRuns(): Flow<List<Run>> {
        return runDao.getRuns()
            .map { runEntities ->
                runEntities.map { it.toRun() }
            }
    }

    override suspend fun upsertRun(run: Run): Result<RunId, DataError.Local> {
        return try {
            val entity = run.toRunEntity()
            runDao.upsertRun(entity)
            Result.Success(entity.id)
        } catch (e: SQLiteFullException) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun upsertRuns(runs: List<Run>): Result<List<RunId>, DataError.Local> {
        return try {
            val entities = runs.map { it.toRunEntity() }
            runDao.upsertRuns(entities)
            Result.Success(entities.map { it.id })
        } catch (e: SQLiteFullException) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun deleteRun(id: String) {
        runDao.deleteRun(id)
    }

    override suspend fun deleteRuns() {
        runDao.deleteAllRuns()
    }
}