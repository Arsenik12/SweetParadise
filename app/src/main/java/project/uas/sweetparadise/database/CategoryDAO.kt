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

    @Query("SELECT * FROM category WHERE id = :categoryId LIMIT 1")
    fun getCategoryById(categoryId: Int): Category?

    @Query("SELECT * FROM category WHERE name = :name LIMIT 1")
    suspend fun getCategoryByName(name: String): Category?
}