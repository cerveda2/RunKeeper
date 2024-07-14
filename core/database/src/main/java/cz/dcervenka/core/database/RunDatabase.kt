package cz.dcervenka.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import cz.dcervenka.core.database.dao.RunDao
import cz.dcervenka.core.database.entity.RunEntity

@Database(
    entities = [RunEntity::class],
    version = 1
)
abstract class RunDatabase : RoomDatabase() {

    abstract val runDao: RunDao


}