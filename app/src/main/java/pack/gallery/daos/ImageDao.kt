package pack.gallery.daos

import androidx.room.Dao
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

    @Query("SELECT * FROM image")
    fun getAll(): Flow<List<Image>>

    @Query("SELECT * FROM image WHERE description LIKE '%' || :query || '%' AND owner = :id")
    fun searchImagesOwner(query: String, id: String): Flow<List<Image>>

    @Query("SELECT * FROM image WHERE description LIKE '%' || :query || '%'")
    fun searchImagesAll(query: String): Flow<List<Image>>

    @Query("SELECT * FROM image WHERE id = :id LIMIT 1")
    suspend fun getID(id: Int): Image

    @Query("UPDATE image SET description=:description WHERE id = :id")
    suspend fun updateDesc(id: Int, description: String): Unit

    @Query("UPDATE image SET filePath=:filepath WHERE id = :id")
    suspend fun updateFilepath(id: Int, filepath: String): Unit

    @Query("DELETE FROM image WHERE id = :id")
    suspend fun delete(id: Int): Unit
}