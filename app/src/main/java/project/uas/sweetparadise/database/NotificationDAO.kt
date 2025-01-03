package project.uas.sweetparadise.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NotificationDAO {
    @Insert
    suspend fun insertNotification(notification: Notification)

    @Query("SELECT * FROM notifications WHERE user_id = :userId ORDER BY id DESC")
    suspend fun getNotificationsByUser(userId: Int): List<Notification>
}