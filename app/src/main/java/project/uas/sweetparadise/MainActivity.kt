package project.uas.sweetparadise

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import project.uas.sweetparadise.database.AppDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var inputUsername: EditText
    private lateinit var inputPassword: EditText
    private lateinit var btnLogin: FrameLayout
    private lateinit var btnGoToRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        inputUsername = findViewById(R.id.inputUsername)
        inputPassword = findViewById(R.id.inputPassword)
        btnLogin = findViewById(R.id.frameLayout)
        btnGoToRegister = findViewById(R.id.Registrasi)

        btnLogin.setOnClickListener {
            val username = inputUsername.text.toString()
            val password = inputPassword.text.toString()
            login(username, password)
        }

        btnGoToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }


    private fun login(username: String, password: String) {
        if (username.isNotEmpty() && password.isNotEmpty()) {
            lifecycleScope.launch(Dispatchers.IO) {
                val user = AppDatabase.getDatabase(this@MainActivity).userDao().login(username, password)
                withContext(Dispatchers.Main) {
                    if (user != null) {
                        Log.d("LoginSuccess", "User ID: ${user.id}")
                        Toast.makeText(this@MainActivity, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@MainActivity, ProfileActivity::class.java)
                        intent.putExtra("USER_ID", user.id)  // utk kirim userId ke MenuActivity
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@MainActivity, "Username/Password Anda Salah, Coba Lagi!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(this, "Masukkan Username dan Password Anda!", Toast.LENGTH_SHORT).show()
        }
    }
}

