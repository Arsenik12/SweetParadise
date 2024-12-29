package project.uas.sweetparadise.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CartDAO {

    @Insert
    suspend fun insertCart(cart: Cart)

    @Query("UPDATE carts SET quantity = :quantity WHERE id = :cartId")
    fun updateCartQuantity(cartId: Int, quantity: Int)

    @Query("DELETE FROM carts WHERE id = :cartId")
    fun deleteItem(cartId: Int)

    @Query("SELECT * FROM carts")
    fun getCartItems(): List<Cart>

    @Query("SELECT * FROM carts WHERE userId = :userId")
    fun getUserCart(userId : Int): List<Cart>

    @Query("""
    SELECT carts.id, carts.userId, carts.menuId, carts.quantity, 
           carts.menuNote as menuNote, menus.name as menuName, menus.price as menuPrice
    FROM carts
    INNER JOIN menus ON carts.menuId = menus.id
""")
    fun getCartWithMenuDetails(): List<CartWithMenu>

    @Query("UPDATE carts SET quantity = :quantity, menuNote = :menuNote WHERE id = :id")
    suspend fun updateCart(id: Int, quantity: Int, menuNote: String?)

    @Query("SELECT * FROM carts WHERE userId = :userId AND menuId = :menuId LIMIT 1")
    suspend fun getCartItemByMenuId(userId: Int, menuId: Int): Cart?
}