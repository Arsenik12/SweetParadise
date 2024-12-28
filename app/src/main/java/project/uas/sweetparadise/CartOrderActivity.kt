package project.uas.sweetparadise

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import project.uas.sweetparadise.database.AppDatabase
import project.uas.sweetparadise.database.CartWithMenu

class CartOrderActivity : AppCompatActivity() {

    private lateinit var adapter: adapterCartOrder
    private var cartItems: MutableList<CartWithMenu> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_order)

        val recyclerView = findViewById<RecyclerView>(R.id.rvOrderMenu)
        val db = AppDatabase.getDatabase(this)

        cartItems = db.cartDao().getCartWithMenuDetails().toMutableList()

        adapter = adapterCartOrder(cartItems.toMutableList())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter.setOnItemClickCallBack(object : adapterCartOrder.OnItemClickCallback {
            override fun minData(dtOrder: CartWithMenu, quantity: Int) {
                if (quantity > 1) {
                    val newQ = quantity - 1
                    db.cartDao().updateCartQuantity(dtOrder.id, newQ)
                    val index = cartItems.indexOfFirst { it.id == dtOrder.id }
                    if (index != -1) {
                        cartItems[index].quantity = newQ
                        adapter.notifyItemChanged(index)
                    }
                } else {
                    db.cartDao().deleteItem(dtOrder.id)
                    val index = cartItems.indexOfFirst { it.id == dtOrder.id }
                    if (index != -1) {
                        cartItems.removeAt(index)
                        adapter.notifyItemRemoved(index)
                    }
                }
            }

            override fun plusData(dtOrder: CartWithMenu, quantity: Int) {
                val newQ = quantity + 1
                db.cartDao().updateCartQuantity(dtOrder.id, newQ)
                val index = cartItems.indexOfFirst { it.id == dtOrder.id }
                if (index != -1) {
                    cartItems[index].quantity = newQ
                    adapter.notifyItemChanged(index)
                }
            }
        })
    }
}
