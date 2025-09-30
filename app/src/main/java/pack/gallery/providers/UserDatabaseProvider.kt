package pack.gallery.providers

import android.content.Context
import androidx.room.Room
import pack.gallery.databases.UserDatabase

object UserDatabaseProvider {
    @Volatile
    private var INSTANCE: UserDatabase? = null

    fun getDatabase(context: Context): UserDatabase {
        return INSTANCE ?: synchronized(this) {
            Room.databaseBuilder(
                        context.applicationContext,
                        UserDatabase::class.java,
                        "user_database"
                    ).fallbackToDestructiveMigration(false).build().also { INSTANCE = it }
        }
    }
}