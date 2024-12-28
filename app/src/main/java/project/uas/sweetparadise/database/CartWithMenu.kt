package project.uas.sweetparadise.database

import androidx.room.Entity

@Entity
data class CartWithMenu(
    val id: Int,
    val userId: Int,
    val menuId: Int,
    var quantity: Int,
    val menuName: String,
    val menuNote: String?,
    val menuPrice: Int
)
