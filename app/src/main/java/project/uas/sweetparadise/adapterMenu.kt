package project.uas.sweetparadise

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import project.uas.sweetparadise.database.Menu

class adapterMenu(private val daftarMenu: MutableList<Menu>) :
    RecyclerView.Adapter<adapterMenu.ListViewHolder>() {

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val _menu = itemView.findViewById<ConstraintLayout>(R.id.Menu)
        val _menuName = itemView.findViewById<TextView>(R.id.menuName)
        val _menuDescription = itemView.findViewById<TextView>(R.id.menuDescription)
        val _menuPrice = itemView.findViewById<TextView>(R.id.menuPrice)
        val _menuFavorite = itemView.findViewById<ImageView>(R.id.ivFav)
        val _menuImage = itemView.findViewById<ImageView>(R.id.menuImage)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.rec_menu, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return daftarMenu.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val dtMenu = daftarMenu[position]
        holder._menuName.text = dtMenu.name
        holder._menuDescription.text = dtMenu.description
        holder._menuPrice.text = "Rp. ${dtMenu.price}"

        if (dtMenu.image.isNotEmpty()) {
            val bitmap = byteArrayToBitmap(dtMenu.image)
            Glide.with(holder.itemView.context)
                .load(bitmap)
                .into(holder._menuImage)
        }

        holder._menuFavorite.setOnClickListener {
            dtMenu.favorite = !dtMenu.favorite
            if (dtMenu.favorite) {
                holder._menuFavorite.setImageResource(R.drawable.heart_icon_chosen)
            } else {
                holder._menuFavorite.setImageResource(R.drawable.heart_icon)
            }
            notifyItemChanged(position)
        }

        holder._menu.setOnClickListener {
            val intent = Intent(holder.itemView.context, MenuDetailActivity::class.java)
            intent.putExtra("id", dtMenu.id)
            intent.putExtra("name", dtMenu.name)
            intent.putExtra("description", dtMenu.description)
            intent.putExtra("price", dtMenu.price)
            intent.putExtra("image", dtMenu.image)
            holder.itemView.context.startActivity(intent)
        }
    }

    fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}
