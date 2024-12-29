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
}