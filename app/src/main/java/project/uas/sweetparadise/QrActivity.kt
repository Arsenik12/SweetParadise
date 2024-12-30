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
import project.uas.sweetparadise.databinding.ActivityQrCodeBinding

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
            Log.e(TAG, "Invalid User ID. Cannot generate QR.")
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
                    val cartIdString = cartId.joinToString(separator = ",")

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

        // PopUp
        val paymentSuccessPopup = findViewById<View>(R.id.paymentSuccessPopup)
        paymentSuccessPopup.visibility = View.VISIBLE
        paymentSuccessPopup.startAnimation(fadeInAnimation)

        // animation selesai, kembali ke main
        fadeInAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                // Hapus cart ID dari database setelah pembayaran sukses
                val userId = intent.getIntExtra("USER_ID", -1)
                if (userId != -1) {
                    deleteCartItems(userId)  // Menghapus cart berdasarkan userId
                }

                val intent = Intent(this@QrActivity, MainActivity::class.java)
                intent.putExtra("USER_ID", userId)
                startActivity(intent)
                finish()
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })
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
}