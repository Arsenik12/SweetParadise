package project.uas.sweetparadise

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import project.uas.sweetparadise.database.Favorite
import project.uas.sweetparadise.database.FavoriteDAO
import project.uas.sweetparadise.database.Menu

class adapterMenu(
    private val daftarMenu: MutableList<Menu>,
    private val activity: Activity,
    private val favoriteDao: FavoriteDAO,
    private val userId: Int
) : RecyclerView.Adapter<adapterMenu.ListViewHolder>() {

    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val menuContainer: ConstraintLayout = itemView.findViewById(R.id.Menu)
        val menuName: TextView = itemView.findViewById(R.id.menuName)
        val menuDescription: TextView = itemView.findViewById(R.id.menuDescription)
        val menuPrice: TextView = itemView.findViewById(R.id.menuPrice)
        val menuFavorite: ImageView = itemView.findViewById(R.id.ivFav)
        val menuImage: ImageView = itemView.findViewById(R.id.menuImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.rec_menu, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int = daftarMenu.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val menu = daftarMenu[position]
        holder.menuName.text = menu.name
        holder.menuDescription.text = menu.description
        holder.menuPrice.text = "Rp. ${menu.price}"

        // Load image
        if (menu.image.isNotEmpty()) {
            val bitmap = byteArrayToBitmap(menu.image)
            Glide.with(holder.itemView.context).load(bitmap).into(holder.menuImage)
        }

        // Check if favorite
        coroutineScope.launch(Dispatchers.IO) {
            val isFavorite = favoriteDao.getFavorite(userId, menu.name) != null
            withContext(Dispatchers.Main) {
                holder.menuFavorite.setImageResource(
                    if (isFavorite) R.drawable.heart_icon_chosen else R.drawable.heart_icon
                )
            }
        }

        // Toggle favorite
        holder.menuFavorite.setOnClickListener {
            coroutineScope.launch(Dispatchers.IO) {
                val existingFavorite = favoriteDao.getFavorite(userId, menu.name)
                if (existingFavorite != null) {
                    favoriteDao.deleteFavorite(userId, menu.name)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(activity, "${menu.name} removed from favorites!", Toast.LENGTH_SHORT).show()
                        holder.menuFavorite.setImageResource(R.drawable.heart_icon)
                    }
                } else {
                    val newFavorite = Favorite(
                        userId = userId,
                        menuName = menu.name,
                        description = menu.description,
                        price = menu.price,
                        image = menu.image,
                        favorite = true,
                        categoryId = menu.categoryId
                    )
                    favoriteDao.insertFavorite(newFavorite)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(activity, "${menu.name} added to favorites!", Toast.LENGTH_SHORT).show()
                        holder.menuFavorite.setImageResource(R.drawable.heart_icon_chosen)
                    }
                }
            }
        }

        // Menu click action
        holder.menuContainer.setOnClickListener {
            val intent = Intent(holder.itemView.context, MenuDetailActivity::class.java).apply {
                putExtra("id", menu.id)
                putExtra("name", menu.name)
                putExtra("description", menu.description)
                putExtra("price", menu.price)
            }
            activity.startActivityForResult(intent, MenuActivity.REQUEST_CODE_ADD_TO_CART)
        }
    }

    private fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}