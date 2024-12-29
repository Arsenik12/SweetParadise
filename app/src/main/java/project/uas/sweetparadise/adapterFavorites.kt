package project.uas.sweetparadise

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import project.uas.sweetparadise.database.Favorite

class adapterFavorites(
    private val daftarFaves: MutableList<Favorite>
) : RecyclerView.Adapter<adapterFavorites.ListViewHolder>() {

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val menuName: TextView = itemView.findViewById(R.id.menuName)
        val menuDescription: TextView = itemView.findViewById(R.id.menuDescription)
        val menuPrice: TextView = itemView.findViewById(R.id.menuPrice)
        val menuImage: ImageView = itemView.findViewById(R.id.menuImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rec_favorites, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int = daftarFaves.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val menu = daftarFaves[position]
        holder.menuName.text = menu.menuName
        holder.menuDescription.text = menu.description
        holder.menuPrice.text = "Rp. ${menu.price}"

        // Load image
        if (menu.image.isNotEmpty()) {
            val bitmap = byteArrayToBitmap(menu.image)
            Glide.with(holder.itemView.context).load(bitmap).into(holder.menuImage)
        }
    }

    private fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}
