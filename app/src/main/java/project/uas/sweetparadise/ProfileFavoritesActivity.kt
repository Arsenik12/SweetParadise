package project.uas.sweetparadise

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import project.uas.sweetparadise.database.AppDatabase
import project.uas.sweetparadise.database.Favorite

class ProfileFavoritesActivity : AppCompatActivity() {

    private lateinit var adapter: adapterFavorites
    private var faveItems: MutableList<Favorite> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_favorites)

        val _rvFHistory = findViewById<RecyclerView>(R.id.rvFHistory)
        val _btnBack = findViewById<ImageView>(R.id.btnBack)

        val db = AppDatabase.getDatabase(this)

        adapter = adapterFavorites(faveItems)
        _rvFHistory.adapter = adapter
        _rvFHistory.layoutManager = LinearLayoutManager(this)

        // Get userId from SharedPreferences
        val sharedPreferences = getSharedPreferences("SweetParadisePrefs", MODE_PRIVATE)
        val userId = sharedPreferences.getInt("CURRENT_USER_ID", -1)

        if (userId == -1) {
            Toast.makeText(this, "No user logged in!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        _btnBack.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // Load bills from database
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val faves = db.favoriteDao().getFavoriteByUserId(userId)

                withContext(Dispatchers.Main) {
                    faveItems.clear()
                    faveItems.addAll(faves)
                    adapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                Log.e("ProfileTransactions", "Error loading favorites: ${e.message}", e)
            }
        }
    }
}
