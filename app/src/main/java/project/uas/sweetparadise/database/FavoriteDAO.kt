package project.uas.sweetparadise.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FavoriteDAO {
    @Insert
    suspend fun insertFavorite(favorite: Favorite)

    @Query("SELECT * FROM favorites WHERE userId = :userId AND menuName = :menuName LIMIT 1")
    suspend fun getFavorite(userId: Int, menuName: String): Favorite?

    @Query("DELETE FROM favorites WHERE userId = :userId AND menuName = :menuName")
    suspend fun deleteFavorite(userId: Int, menuName: String)


}