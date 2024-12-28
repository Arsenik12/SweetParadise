package project.uas.sweetparadise.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MenuDAO {
    @Insert
    suspend fun insertMenu(menu: Menu)

    @Query("SELECT * FROM menus")
    fun getAllMenus(): List<Menu>

    @Query("SELECT * FROM menus WHERE id = :menuId")
    fun addmenu(menuId: Int): Menu?

    @Query("SELECT * FROM menus WHERE categoryId = :categoryId")
    fun addmenu(categoryId: String): Menu?

}