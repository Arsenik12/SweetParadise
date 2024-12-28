package project.uas.sweetparadise.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import project.uas.sweetparadise.DAO.CartDAO
import project.uas.sweetparadise.DAO.CategoryDAO
import project.uas.sweetparadise.DAO.MenuDAO
import project.uas.sweetparadise.DAO.UserDAO
import project.uas.sweetparadise.Entity.Cart
import project.uas.sweetparadise.Entity.Category
import project.uas.sweetparadise.Entity.Menu
import project.uas.sweetparadise.Entity.User

@Database(entities = [User::class, Cart::class, Menu::class, Category::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDAO
    abstract fun cartDao(): CartDAO
    abstract fun menuDao(): MenuDAO
    abstract fun categoryDao(): CategoryDAO

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