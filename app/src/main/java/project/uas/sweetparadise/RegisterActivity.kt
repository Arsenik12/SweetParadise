package project.uas.sweetparadise

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import project.uas.sweetparadise.database.AppDatabase
import project.uas.sweetparadise.database.User

class RegisterActivity : AppCompatActivity() {

    private lateinit var inputUsername: EditText
    private lateinit var inputPassword: EditText
    private lateinit var btnRegister: FrameLayout
    private lateinit var btnGoToLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrasi)

        inputUsername = findViewById(R.id.inputUsername)
        inputPassword = findViewById(R.id.inputPassword)
        btnRegister = findViewById(R.id.frameLayout)
        btnGoToLogin = findViewById(R.id.Masuk)

        btnRegister.setOnClickListener {
            val username = inputUsername.text.toString()
            val password = inputPassword.text.toString()
            register(username, password)
        }

        btnGoToLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun register(username: String, password: String) {
        if (username.isNotEmpty() && password.isNotEmpty()) {
            val user = User(username = username, password = password)
            lifecycleScope.launch(Dispatchers.IO) {
                AppDatabase.getDatabase(this@RegisterActivity).userDao().insertUser(user)
                runOnUiThread {
                    Toast.makeText(this@RegisterActivity, "Registrasi Berhasil!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        } else {
            Toast.makeText(this, "Harap Isi Username dan Password Anda!", Toast.LENGTH_SHORT).show()
        }
    }
}
