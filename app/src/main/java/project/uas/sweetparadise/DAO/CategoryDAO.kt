package project.uas.sweetparadise.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import project.uas.sweetparadise.Entity.Category

@Dao
interface CategoryDAO {
    @Insert
    fun insertCategory(category: Category)

    @Query("SELECT * FROM category")
    fun getAllCategories(): List<Category>
}