package project.uas.sweetparadise.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CartDAO {

    @Insert
    suspend fun insertCart(cart: Cart)

    @Query("SELECT * FROM carts")
    suspend fun getCartItems(): List<Cart>

    @Update
    suspend fun updateCart(cart: Cart)

    @Query("UPDATE carts SET quantity = :newQuantity WHERE id = :cartId")
    suspend fun updateCartQuantity(cartId: Int, newQuantity: Int)

    @Query("DELETE FROM carts WHERE id = :cartId")
    suspend fun deleteItem(cartId: Int)

    @Query("SELECT * FROM carts WHERE userId = :userId")
    suspend fun getUserCart(userId : Int): List<Cart>

}