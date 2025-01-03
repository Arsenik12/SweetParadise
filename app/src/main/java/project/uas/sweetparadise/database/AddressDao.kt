package project.uas.sweetparadise.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AddressDao {
    @Insert
    suspend fun insertAddress(address: AddressEntity)

    @Query("SELECT * FROM address_table")
    suspend fun getAllAddresses(): List<AddressEntity>

    @Query("SELECT * FROM address_table WHERE userId = :userId LIMIT 1")
    suspend fun getAddressByUserId(userId: Int): AddressEntity?


}
