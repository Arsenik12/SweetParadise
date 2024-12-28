package project.uas.sweetparadise.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import project.uas.sweetparadise.Entity.Menu

@Dao
interface MenuDAO {
    @Insert
    suspend fun insertMenu(menu: Menu)

    @Query("SELECT * FROM menus")
    fun getAllMenus(): List<Menu>

    @Query("SELECT * FROM menus WHERE id = :menuId")
    fun getMenu(menuId: Int): Menu?

    @Query("SELECT * FROM menus WHERE categoryId = :categoryId")
    fun getMenu(categoryId: String): Menu?

    @Query("SELECT image FROM menus WHERE id = :Id")
    suspend fun getImage(Id: Int): ByteArray
}