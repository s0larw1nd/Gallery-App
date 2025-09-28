package pack.gallery

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Image (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val filePath: String,
    val description: String,
    val owner: Int
)