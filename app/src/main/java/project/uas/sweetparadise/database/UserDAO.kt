package project.uas.sweetparadise.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDAO {

    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    suspend fun login(username: String, password: String): User?

    @Query("SELECT id FROM users WHERE id = :Id")
    suspend fun getId(Id: Int): Int

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Int): User?

    @Query("SELECT id FROM users WHERE username = :username")
    suspend fun getCurrentUserId(username: String): Int

    @Query("SELECT username FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUsernameById(userId: Int): String
}

