package project.uas.sweetparadise.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HistoryDAO {
    @Insert
    suspend fun insertHistory(history: History)

    @Query("SELECT * FROM history")
    suspend fun getHistoryItems(): List<History>

    @Query("SELECT * FROM history WHERE userId = :userId")
    suspend fun getUserHistory(userId : Int): List<History>

    @Query("SELECT id FROM history WHERE userId = :userId")
    suspend fun getHistoryIdsByUser(userId: Int): List<Int>

    @Query("SELECT * FROM history WHERE userId = :userId AND menuId = :menuId LIMIT 1")
    suspend fun getHistoryItemByMenuId(userId: Int, menuId: Int): Cart?

    @Query("SELECT * FROM history WHERE userId = :userId")
    suspend fun getHistoryByUserId(userId: Int): List<Cart>
}