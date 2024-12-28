package project.uas.sweetparadise.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import project.uas.sweetparadise.DAO.AddressDao
import project.uas.sweetparadise.Entity.AddressEntity

@Database(entities = [AddressEntity::class], version = 1, exportSchema = false)
abstract class AddressDatabase : RoomDatabase() {
    abstract fun addressDao(): AddressDao

    companion object {
        @Volatile
        private var INSTANCE: AddressDatabase? = null

        fun getDatabase(context: Context): AddressDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AddressDatabase::class.java,
                    "address_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
