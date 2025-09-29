package pack.gallery.daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import pack.gallery.entities.Image

@Dao
interface ImageDao {
    @Insert
    suspend fun insert(image: Image)

    @Update
    suspend fun update(image: Image)

    @Delete
    suspend fun delete(image: Image)

    @Query("SELECT * FROM image")
    suspend fun getAll(): List<Image>

    @Query("SELECT * FROM image WHERE id = :id LIMIT 1")
    suspend fun getID(id: Int): Image
}