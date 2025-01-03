package project.uas.sweetparadise

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import project.uas.sweetparadise.database.AppDatabase
import project.uas.sweetparadise.database.Cart
import java.text.NumberFormat
import java.util.Locale

class adapterCartOrder(private val daftarOrder : MutableList<Cart>, private val db: AppDatabase) :
    RecyclerView.Adapter<adapterCartOrder.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun minData(dtOrder: Cart, quantity: Int)
        fun plusData(dtOrder: Cart, quantity: Int)
    }

    fun setOnItemClickCallBack(onItemClickCallBack: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallBack
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val _menuName = itemView.findViewById<TextView>(R.id.menuName)
        val _menuNote = itemView.findViewById<TextView>(R.id.menuNote)
        val _menuQuantity = itemView.findViewById<TextView>(R.id.menuQuantity)
        val _menuPrice = itemView.findViewById<TextView>(R.id.menuPrice)
        val _menuImage = itemView.findViewById<ImageView>(R.id.menuImage)

        var _btnMin = itemView.findViewById<ImageView>(R.id.iv2)
        var _btnMax = itemView.findViewById<ImageView>(R.id.iv3)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.rec_cart, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return daftarOrder.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val dtOrder = daftarOrder[position]

        CoroutineScope(Dispatchers.IO).launch {
            val menu = db.menuDao().getMenuById(dtOrder.menuId) // Ambil data menu berdasarkan menuId

            // Periksa apakah menu dan gambar ada
            if (menu?.image?.isNotEmpty() == true) {
                val bitmap = byteArrayToBitmap(menu.image)

                withContext(Dispatchers.Main) {
                    // cek menuImage tidak null sebelum memuat gambar
                    if (holder._menuImage != null) {
                        Glide.with(holder.itemView.context).load(bitmap).into(holder._menuImage)
                    } else {
                        // Gambar default jika menuImage null
                        holder._menuImage?.setImageResource(R.drawable.red_velvet)
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    // Jika tidak ada gambar, set gambar default
                    holder._menuImage.setImageResource(R.drawable.red_velvet)
                }
            }

            if (menu != null) {
                withContext(Dispatchers.Main) {
                    holder._menuName.text = menu.name // Menampilkan nama menu
                    holder._menuNote.text = dtOrder.menuNote?.takeIf { it.isNotEmpty() } ?: "(no notes)" // Menampilkan menuNote
                    holder._menuNote.setTextColor(Color.parseColor("#949494"))
                }
            } else {
                withContext(Dispatchers.Main) {
                    holder._menuName.text = "Menu Tidak Ditemukan" // Tampilkan pesan jika menu tidak ditemukan
                    holder._menuNote.text = "(no notes)" // Default jika tidak ada catatan
                    holder._menuNote.setTextColor(Color.parseColor("#949494"))
                }
            }
        }

        var menuPrice = dtOrder.price * dtOrder.quantity

        holder._menuPrice.text = "Rp ${formatToRupiah(menuPrice)}"
        holder._menuQuantity.text = dtOrder.quantity.toString()

        holder._btnMin.setOnClickListener {
            if (dtOrder.quantity > 1) {
                val newQuantity = dtOrder.quantity - 1
                dtOrder.quantity = newQuantity
                onItemClickCallback.minData(dtOrder, newQuantity) // Update data
                notifyItemChanged(position) // Notify adapter utk UI update
            }
        }

        holder._btnMax.setOnClickListener {
            val newQuantity = dtOrder.quantity + 1
            dtOrder.quantity = newQuantity
            onItemClickCallback.plusData(dtOrder, newQuantity) // Update data
            notifyItemChanged(position) // Notify adapter utk UI update
        }
    }

    fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    fun formatToRupiah(amount: Int): String {
        val format = NumberFormat.getInstance(Locale("id", "ID")) // Format Rupiah
        return format.format(amount)
    }
}