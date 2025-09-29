package pack.gallery.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import pack.gallery.entities.User

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM User WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?
}