package project.uas.sweetparadise

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import project.uas.sweetparadise.Entity.CartWithMenu

class adapterCartOrder(private val daftarOrder: MutableList<CartWithMenu>) :
    RecyclerView.Adapter<adapterCartOrder.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun minData(dtOrder: CartWithMenu, quantity: Int)
        fun plusData(dtOrder: CartWithMenu, quantity: Int)
    }

    fun setOnItemClickCallBack(onItemClickCallBack: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallBack
    }

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val menuName: TextView = itemView.findViewById(R.id.menuName)
        val menuNote: TextView = itemView.findViewById(R.id.menuNote)
        val menuQuantity: TextView = itemView.findViewById(R.id.menuQuantity)
        val menuPrice: TextView = itemView.findViewById(R.id.menuPrice)

        val btnMin: ImageView = itemView.findViewById(R.id.iv2)
        val btnMax: ImageView = itemView.findViewById(R.id.iv3)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_cart_order, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return daftarOrder.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val dtOrder = daftarOrder[position]
        holder.menuName.text = dtOrder.menuName
        holder.menuNote.text = dtOrder.menuNote?.takeIf { it.isNotEmpty() } ?: "(no notes)"
        holder.menuNote.setTextColor(Color.parseColor("#949494"))
        holder.menuPrice.text = "Rp ${dtOrder.menuPrice * dtOrder.quantity}"
        holder.menuQuantity.text = dtOrder.quantity.toString()

        holder.btnMin.setOnClickListener {
            if (dtOrder.quantity > 1) {
                onItemClickCallback.minData(dtOrder, dtOrder.quantity)
            }
        }

        holder.btnMax.setOnClickListener {
            onItemClickCallback.plusData(dtOrder, dtOrder.quantity + 1)
        }
    }
}
