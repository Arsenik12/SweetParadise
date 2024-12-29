package project.uas.sweetparadise

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject
import project.uas.sweetparadise.Entity.CartWithMenu

class CartOrderActivity : AppCompatActivity(), PaymentResultListener {

    private lateinit var adapter: adapterCartOrder
    private var cartItems: MutableList<CartWithMenu> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_order)

        val recyclerView = findViewById<RecyclerView>(R.id.rvOrderMenu)

        // Data mock untuk testing
        cartItems = mutableListOf(
            CartWithMenu(1, 1, 1, 1, "Reese's Fudge", "no notes", 27000),
            CartWithMenu(2, 1, 2, 1, "Strawberry Shortcake", "no notes", 23000)
        )

        adapter = adapterCartOrder(cartItems.toMutableList())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter.setOnItemClickCallBack(object : adapterCartOrder.OnItemClickCallback {
            override fun minData(dtOrder: CartWithMenu, quantity: Int) {
                if (quantity > 1) {
                    val newQ = quantity - 1
                    val index = cartItems.indexOfFirst { it.id == dtOrder.id }
                    if (index != -1) {
                        cartItems[index].quantity = newQ
                        adapter.notifyItemChanged(index)
                    }
                } else {
                    val index = cartItems.indexOfFirst { it.id == dtOrder.id }
                    if (index != -1) {
                        cartItems.removeAt(index)
                        adapter.notifyItemRemoved(index)
                    }
                }
            }

            override fun plusData(dtOrder: CartWithMenu, quantity: Int) {
                val newQ = quantity + 1
                val index = cartItems.indexOfFirst { it.id == dtOrder.id }
                if (index != -1) {
                    cartItems[index].quantity = newQ
                    adapter.notifyItemChanged(index)
                }
            }
        })

        // Tombol untuk Razorpay
        val onlinePaymentButton = findViewById<FrameLayout>(R.id.btnOnlinePayment)
        onlinePaymentButton.setOnClickListener {
            startPayment()
        }
    }

    private fun startPayment() {
        val checkout = Checkout()
        checkout.setKeyID("rzp_test_sg_wvZZ1erqsP9kRk") // Ganti dengan Test Key ID dari Razorpay Dashboard

        try {
            val options = JSONObject()
            options.put("name", "Sweet Paradise")
            options.put("description", "Order Payment")
            options.put("currency", "INR")
            options.put("amount", calculateTotalAmount() * 100) // Razorpay memerlukan nilai dalam paise

//            // Informasi pengguna (opsional)
//            val prefill = JSONObject()
//            prefill.put("email", "user@example.com")
//            prefill.put("contact", "08123456789")
//            options.put("prefill", prefill)

            checkout.open(this, options)
        } catch (e: Exception) {
            Toast.makeText(this, "Error in payment: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun calculateTotalAmount(): Int {
        return cartItems.sumOf { it.menuPrice * it.quantity }
    }

    override fun onPaymentSuccess(razorpayPaymentID: String?) {
        Toast.makeText(this, "Payment Successful: $razorpayPaymentID", Toast.LENGTH_LONG).show()
    }

    override fun onPaymentError(code: Int, response: String?) {
        Toast.makeText(this, "Payment Failed: $response", Toast.LENGTH_LONG).show()
    }
}
