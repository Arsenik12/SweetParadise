package project.uas.sweetparadise.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CategoryDAO {
    @Insert
    fun insertCategory(category: Category)

    @Query("SELECT * FROM category")
    fun getAllCategories(): List<Category>
}