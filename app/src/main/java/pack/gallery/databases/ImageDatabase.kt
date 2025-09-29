package pack.gallery.databases

import androidx.room.Database
import androidx.room.RoomDatabase
import pack.gallery.entities.Image
import pack.gallery.daos.ImageDao

@Database(entities = [Image::class], version = 2)
abstract class ImageDatabase : RoomDatabase() {
    abstract fun imageDao(): ImageDao
}