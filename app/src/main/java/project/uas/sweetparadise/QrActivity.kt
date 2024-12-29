package project.uas.sweetparadise

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityQrCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = intent.getIntExtra("USER_ID", -1)
        if (userId != -1) {
            generateQRCode(userId)
        } else {
            Log.e(TAG, "Invalid User ID.")
        }

        val _btnDone = findViewById<Button>(R.id.btnDonePayment)
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

                    // Generate QR code di thread utama
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
}