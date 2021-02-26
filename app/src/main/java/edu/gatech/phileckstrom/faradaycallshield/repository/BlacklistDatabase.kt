package edu.gatech.phileckstrom.faradaycallshield.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import edu.gatech.phileckstrom.faradaycallshield.repository.dao.BlackListEntryDao
import edu.gatech.phileckstrom.faradaycallshield.repository.dao.DateActivityDao
import edu.gatech.phileckstrom.faradaycallshield.repository.dao.DefaultSMSItemDao
import edu.gatech.phileckstrom.faradaycallshield.repository.entities.ActivityLogItem
import edu.gatech.phileckstrom.faradaycallshield.repository.entities.BlackListEntry
import edu.gatech.phileckstrom.faradaycallshield.repository.entities.DefaultSMSItem

@Database(
        entities = [BlackListEntry::class, ActivityLogItem::class, DefaultSMSItem::class],
        version = 3,
        exportSchema = false
)


//Currently unused
abstract class BlacklistDatabase : RoomDatabase() {

    abstract fun blackListEntryDao(): BlackListEntryDao
    abstract fun dateActivityDao(): DateActivityDao
    abstract fun defaultSMSItemDao(): DefaultSMSItemDao

    companion object{
        @Volatile private var instance : BlacklistDatabase? = null
        private val LOCK = Any()

        operator fun invoke(conext: Context) = instance ?: synchronized(LOCK){
            instance ?: buildDatabase(conext).also { instance = it }
        }

        public fun buildDatabase(context: Context) = Room.databaseBuilder(
                context.applicationContext,
                BlacklistDatabase::class.java,
                "notedatabase"
        ).build()
    }
}