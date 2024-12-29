package project.uas.sweetparadise.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bills")
data class Bill(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val userId: Int,

    val dateTime: String,

    val quantity: Int,

    val totalPrice: Int
)
