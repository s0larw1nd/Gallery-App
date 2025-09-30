package pack.gallery.repositories

import kotlinx.coroutines.flow.Flow
import pack.gallery.daos.ImageDao
import pack.gallery.entities.Image

class ImageRepository(private val imageDao: ImageDao) {
    fun all() : Flow<List<Image>> {
        return imageDao.getAll()
    }
    fun searchOwner(description: String, id: String) : Flow<List<Image>> {
        return imageDao.searchImagesOwner(description, id)
    }
    fun searchAll(description: String) : Flow<List<Image>> {
        return imageDao.searchImagesAll(description)
    }
    suspend fun searchID(id: Int) : Image {
        return imageDao.getID(id)
    }
    suspend fun updateDescID(id: Int, description: String) {
        return imageDao.updateDesc(id, description)
    }
    suspend fun insert(image: Image) = imageDao.insert(image)
    suspend fun delete(id: Int) = imageDao.delete(id)
}