package project.uas.sweetparadise

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import project.uas.sweetparadise.database.AppDatabase
import project.uas.sweetparadise.database.Bill
import project.uas.sweetparadise.database.History
import project.uas.sweetparadise.database.Notification
import project.uas.sweetparadise.databinding.ActivityQrCodeBinding
import project.uas.sweetparadise.helper.DateHelper.getCurrentDate
import project.uas.sweetparadise.helper.TimeHelper.getCurrentTime
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG = "QrActivity"
private const val QR_SIZE = 1024

class QrActivity : AppCompatActivity() {

    private lateinit var binding : ActivityQrCodeBinding
    private var userId: Int = -1 // Tambahkan variabel global untuk userId

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityQrCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("SweetParadisePrefs", MODE_PRIVATE)
        userId = sharedPreferences.getInt("CURRENT_USER_ID", -1)

        if (userId == -1) {
            Toast.makeText(this, "No user logged in!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        if (userId != -1) {
            generateQRCode(userId)
        } else {
            Log.e(TAG, "Invalid User ID.")
        }

        val _btnDone = findViewById<Button>(R.id.btnDonePayment)
        _btnDone.setOnClickListener {
            showPaymentSuccessPopup()
        }
    }

    private fun generateQRCode(userId: Int) {
        val db = AppDatabase.getDatabase(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Ambil daftar cart id dari database berdasarkan userId
                val cartId = db.cartDao().getCartIdsByUser(userId)

                if (cartId.isNotEmpty()) {
                    // Gabungkan semua cart id menjadi string untuk QR
                    val cartIdString = cartId.joinToString(separator = ", ")

                    // Generate QR code
                    withContext(Dispatchers.Main) {
                        try {
                            val encoder = BarcodeEncoder()
                            val bitmap = encoder.encodeBitmap(
                                cartIdString,
                                BarcodeFormat.QR_CODE,
                                QR_SIZE,
                                QR_SIZE
                            )

                            binding.generatedQrImage.setImageBitmap(bitmap)
                            Log.d(TAG, "QR Code generated with cart IDs: $cartIdString")

                        } catch (e: WriterException) {
                            Log.e(TAG, "Error generating QR Code: ${e.message}")
                        }
                    }
                } else {
                    Log.e(TAG, "No cart IDs found for user.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching cart IDs: ${e.message}")
            }
        }
    }

    private fun showPaymentSuccessPopup() {
        // create animation
        val fadeInAnimation: Animation = AnimationUtils.loadAnimation(this, R.anim.fade_in)

        // Popup animation
        val paymentSuccessPopup = findViewById<View>(R.id.paymentSuccessPopup)
        paymentSuccessPopup.visibility = View.VISIBLE

        // Apply the animation
        paymentSuccessPopup.startAnimation(fadeInAnimation)

        // Once the animation ends, navigate back to the homepage
        fadeInAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                val userId = intent.getIntExtra("USER_ID", -1)
                val isPointsUsed = intent.getBooleanExtra("IS_POINTS_USED", false)
                if (userId != -1) {
                    CoroutineScope(Dispatchers.Main).launch {
                        val additionalPoints = calculateAdditionalPoints(userId)
                        if (additionalPoints > 0) {
                            // Poin tambahan hanya diperbarui jika ada tambahan
                            updateUserPoints(userId, additionalPoints, isPointsUsed)
                        }
                        saveBillToDatabase(userId) // Move this function here
                        saveCartToHistory(userId)
                        // Hapus item keranjang setelah pembayaran sukses
                        deleteCartItems(userId)
                    }
                }
                // Navigate back to homepage
                val intent = Intent(this@QrActivity, MenuTypeActivity::class.java)
                intent.putExtra("USER_ID", userId)
                startActivity(intent)
                finish()
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }

    private suspend fun calculateAdditionalPoints(userId: Int): Int {
        val db = AppDatabase.getDatabase(this)
        return withContext(Dispatchers.IO) {
            try {
                val carts = db.cartDao().getUserCart(userId)
                val priceAmount = carts.sumOf { it.price * it.quantity }
                (priceAmount * 0.03).toInt()
            } catch (e: Exception) {
                Log.e(TAG, "Error calculating additional points: ${e.message}")
                0
            }
        }
    }

    private fun saveBillToDatabase(userId: Int) {
        val db = AppDatabase.getDatabase(this)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val carts = db.cartDao().getUserCart(userId)
                val totalQuantity = carts.sumOf { it.quantity }
                val priceAmount = carts.sumOf { it.price * it.quantity }
                val taxAmount = priceAmount * 0.1
                val totalAmount = priceAmount + taxAmount
                val bill = Bill(
                    userId = userId,
                    date = getCurrentDate(),
                    time = getCurrentTime(),
                    quantity = totalQuantity,
                    totalPrice = totalAmount.toInt()
                )
                db.billDao().insertBill(bill)
                Log.d(TAG, "Bill saved successfully!")
            } catch (e: Exception) {
                Log.e(TAG, "Error saving bill: ${e.message}")
            }
        }
    }

    private fun deleteCartItems(userId: Int) {
        val db = AppDatabase.getDatabase(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Menghapus cart berdasarkan userId
                db.cartDao().deleteCartItemsAfterPaymentSuccess(userId)
                Log.d(TAG, "Cart items deleted for user ID: $userId")
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting cart items: ${e.message}")
            }
        }
    }

    private fun updateUserPoints(userId: Int, additionalPoints: Int, isPointsUsed: Boolean) {
        val db = AppDatabase.getDatabase(this)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "Updating points. isPointsUsed: $isPointsUsed")
                if (isPointsUsed) {
                    db.userDao().updateUserPoints(userId, 0)
                    withContext(Dispatchers.Main) {
                        Log.d(TAG, "User points reset to 0 after usage.")
                    }
                } else {
                    val currentPoints = db.userDao().getUserPoints(userId) ?: 0
                    val updatedPoints = currentPoints + additionalPoints

                    // Perbarui poin di database
                    db.userDao().updateUserPoints(userId, updatedPoints)

                    // Tambahkan notifikasi ke database
                    addNotification(userId, additionalPoints)

                    withContext(Dispatchers.Main) {
                        Log.d(TAG, "User points updated successfully. New points: $updatedPoints")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating user points: ${e.message}")
            }
        }
    }

    private fun addNotification(userId: Int, pointsEarned: Int) {
        val db = AppDatabase.getDatabase(this)
        val message = "You earned $pointsEarned points from your last transaction!"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                db.notificationDao().insertNotification(
                    Notification(
                        userId = userId,
                        message = message,
                        date = getCurrentDate()
                    )
                )
                Log.d(TAG, "Notification added for user ID: $userId")
            } catch (e: Exception) {
                Log.e(TAG, "Error adding notification: ${e.message}")
            }
        }
    }


    private fun saveCartToHistory(userId: Int) {
        val db = AppDatabase.getDatabase(this)
        val status = intent.getIntExtra("STATUS", 0) // Default to 0 if STATUS is not provided

        CoroutineScope(Dispatchers.IO).launch {
            val cartItems = db.cartDao().getCartByUserId(userId)

            cartItems.forEach { item ->
                db.historyDao().insertHistory(
                    History(
                        userId = userId,
                        menuId = item.menuId,
                        price = item.price,
                        quantity = item.quantity,
                        menuNote = item.menuNote,
                        date = getCurrentDate(),
                        time = getCurrentTime(),
                        status = status
                    )
                )
            }
        }
    }

    private fun getCurrentDate(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return formatter.format(Date())
    }

}