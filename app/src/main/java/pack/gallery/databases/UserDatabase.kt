package pack.gallery.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import pack.gallery.daos.UserDao
import pack.gallery.entities.User

@Database(entities = [User::class], version = 2)
abstract class UserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}