package project.uas.sweetparadise.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import project.uas.sweetparadise.Entity.User

@Dao
interface UserDAO {

    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    suspend fun login(username: String, password: String): User?
}

