package project.uas.sweetparadise.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BillDAO {

    @Insert
    suspend fun insertBill(bill: Bill)

    @Query("SELECT * FROM bills")
    suspend fun getBillItems(): List<Bill>

    @Query("SELECT * FROM bills WHERE userId = :userId")
    suspend fun getBillsByUserId(userId: Int): List<Bill>

    @Query("SELECT * FROM bills WHERE userId = :userId ORDER BY date DESC, time DESC LIMIT 1")
    suspend fun getMostRecentBillByUserId(userId: Int): Bill?

}