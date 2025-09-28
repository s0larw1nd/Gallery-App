package pack.gallery

import android.content.Context
import android.util.Log
import androidx.room.Room

object ImageDatabaseProvider {
    @Volatile
    private var INSTANCE: ImageDatabase? = null

    fun getDatabase(context: Context): ImageDatabase {
        return INSTANCE ?: synchronized(this) {
            Room.databaseBuilder(
                context.applicationContext,
                ImageDatabase::class.java,
                "app_database"
            ).build().also { INSTANCE = it }
        }
    }
}