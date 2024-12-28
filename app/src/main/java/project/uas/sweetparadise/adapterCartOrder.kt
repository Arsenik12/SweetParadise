package project.uas.sweetparadise

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import project.uas.sweetparadise.database.Cart
import project.uas.sweetparadise.database.CartWithMenu

class adapterCartOrder(private val daftarOrder : MutableList<CartWithMenu>) :
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
        val _menuName = itemView.findViewById<TextView>(R.id.menuName)
        val _menuNote = itemView.findViewById<TextView>(R.id.menuNote)
        val _menuQuantity = itemView.findViewById<TextView>(R.id.menuQuantity)
        val _menuPrice = itemView.findViewById<TextView>(R.id.menuPrice)

        var _btnMin = itemView.findViewById<ImageView>(R.id.iv2)
        var _btnMax = itemView.findViewById<ImageView>(R.id.iv3)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): adapterCartOrder.ListViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.activity_cart_order, parent, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return daftarOrder.size
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val dtOrder = daftarOrder[position]
        holder._menuName.text = dtOrder.menuName
        holder._menuNote.text = dtOrder.menuNote?.takeIf { it.isNotEmpty() } ?: "(no notes)"
        holder._menuNote.setTextColor(Color.parseColor("#949494"))
        holder._menuPrice.text = "Rp ${dtOrder.menuPrice * dtOrder.quantity}"
        holder._menuQuantity.text = dtOrder.quantity.toString()

        holder._btnMin.setOnClickListener {
            if (dtOrder.quantity > 1) {
                onItemClickCallback.minData(dtOrder, dtOrder.quantity)
            }
        }

        holder._btnMax.setOnClickListener {
            onItemClickCallback.plusData(dtOrder, dtOrder.quantity + 1)
        }
    }



}