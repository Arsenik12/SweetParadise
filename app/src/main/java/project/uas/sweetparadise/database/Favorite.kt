package project.uas.sweetparadise.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorites",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["Id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class Favorite(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "userId")
    val userId: Int,

    @ColumnInfo(name = "menuName")
    val menuName: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "price")
    val price: Int,

    @ColumnInfo(name = "image")
    val image: ByteArray,

    @ColumnInfo(name = "favorite")
    var favorite: Boolean = false,

    @ColumnInfo(name = "categoryId")
    val categoryId: Int
)
