package project.uas.sweetparadise.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import project.uas.sweetparadise.Entity.AddressEntity

@Dao
interface AddressDao {
    @Insert
    suspend fun insertAddress(address: AddressEntity)

    @Query("SELECT * FROM address_table")
    suspend fun getAllAddresses(): List<AddressEntity>
}
