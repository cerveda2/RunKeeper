package cz.dcervenka.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import cz.dcervenka.core.database.entity.RunPendingSyncEntity

@Dao
interface RunPendingSyncDao {

    // CREATED RUNS

    @Query("SELECT * FROM runpendingsyncentity WHERE userId = :userId")
    suspend fun getAllRunPendingSyncEntities(userId: String): List<RunPendingSyncEntity>

    @Query("SELECT * FROM runpendingsyncentity WHERE runId = :runId")
    suspend fun getRunPendingSyncEntity(runId: String): RunPendingSyncEntity?

    suspend fun upsertRunPendingSyncEntity(entity: RunPendingSyncEntity)

    @Query("DELETE FROM runpendingsyncentity WHERE runId = :runId")
    suspend fun deleteRunPendingSyncEntity(runId: String)

    // DELETED RUNS

    @Query("SELECT * FROM deletedrunsyncentity WHERE userId = :userId")
    suspend fun getAllDeletedRunSyncEntities(userId: String): List<RunPendingSyncEntity>

    suspend fun upsertDeletedRunSyncEntity(entity: RunPendingSyncEntity)

    @Query("DELETE FROM deletedrunsyncentity WHERE runId = :runId")
    suspend fun deleteDeletedRunSyncEntity(runId: String)
}