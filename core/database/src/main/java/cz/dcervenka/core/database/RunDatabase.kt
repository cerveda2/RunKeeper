package cz.dcervenka.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import cz.dcervenka.core.database.dao.AnalyticsDao
import cz.dcervenka.core.database.dao.RunDao
import cz.dcervenka.core.database.dao.RunPendingSyncDao
import cz.dcervenka.core.database.entity.DeletedRunSyncEntity
import cz.dcervenka.core.database.entity.RunEntity
import cz.dcervenka.core.database.entity.RunPendingSyncEntity

@Database(
    entities = [
        RunEntity::class,
        RunPendingSyncEntity::class,
        DeletedRunSyncEntity::class
    ],
    version = 1
)
abstract class RunDatabase : RoomDatabase() {

    abstract val runDao: RunDao
    abstract val runPendingSyncDao: RunPendingSyncDao
    abstract val analyticsDao: AnalyticsDao
}