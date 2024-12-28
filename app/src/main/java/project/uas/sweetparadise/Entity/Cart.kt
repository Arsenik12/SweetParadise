package project.uas.sweetparadise.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import project.uas.sweetparadise.Entity.Menu

@Entity(
    tableName = "carts",
    foreignKeys = [
        ForeignKey(
            entity = Menu::class,
            parentColumns = ["id"],
            childColumns = ["menuId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class Cart(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "userId")
    val userId: Int,

    @ColumnInfo(name = "menuId")
    val menuId: Int,

    @ColumnInfo(name = "quantity")
    val quantity: Int,

    @ColumnInfo(name = "menuNote")
    val menuNote: String? = null
)