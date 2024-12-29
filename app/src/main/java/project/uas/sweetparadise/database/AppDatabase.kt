package project.uas.sweetparadise.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [User::class, Cart::class, Menu::class, Category::class, Favorite::class],
    version = 1,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDAO
    abstract fun cartDao(): CartDAO
    abstract fun menuDao(): MenuDAO
    abstract fun categoryDao(): CategoryDAO
    abstract fun favoriteDao(): FavoriteDAO


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}