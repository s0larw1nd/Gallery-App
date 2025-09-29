package pack.gallery.providers

import android.content.Context
import androidx.room.Room
import pack.gallery.databases.ImageDatabase

object ImageDatabaseProvider {
    @Volatile
    private var INSTANCE: ImageDatabase? = null

    fun getDatabase(context: Context): ImageDatabase {
        return INSTANCE ?: synchronized(this) {
            Room.databaseBuilder(
                context.applicationContext,
                ImageDatabase::class.java,
                "image_database"
            ).build().also { INSTANCE = it }
        }
    }
}