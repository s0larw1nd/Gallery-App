package pack.gallery
import androidx.room.*

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
}