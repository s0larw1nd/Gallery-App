package pack.gallery.viewmodels

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pack.gallery.entities.Image
import pack.gallery.repositories.ImageRepository
import pack.gallery.repositories.PreferencesRepository
import java.io.File
import java.io.FileOutputStream

class ImageViewModel(
    private val imageRepository: ImageRepository,
    private val prefsRepository: PreferencesRepository
    ) : ViewModel() {
    private val searchQuery = MutableStateFlow("")

    val imagesOwner: StateFlow<List<Image>> = searchQuery
        .debounce(300)
        .flatMapLatest { query ->
            imageRepository.searchOwner(query, prefsRepository.getString("user_id").toString())
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val imagesAll: StateFlow<List<Image>> = searchQuery
        .debounce(1000)
        .flatMapLatest { query ->
            imageRepository.searchAll(query)
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    suspend fun searchID(id: Int) : Image {
        val image = imageRepository.searchID(id)
        return image
    }
    suspend fun updateDescId(id: Int, description: String) {
        imageRepository.updateDescID(id,description)
    }
    suspend fun deleteId(id: Int) {
        imageRepository.delete(id)
    }

    fun saveImageToInternalStorage(uri: Uri, contentResolver: ContentResolver, filesDir: File): Unit {
        viewModelScope.launch(Dispatchers.IO) {
            val inputStream = contentResolver.openInputStream(uri)
            val fileName = "photo_${System.currentTimeMillis()}.jpg"
            val file = File(filesDir, fileName)
            val outputStream = FileOutputStream(file)

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            val id = prefsRepository.getString("user_id")

            imageRepository.insert(
                Image(
                    filePath = file.absolutePath,
                    description = "",
                    owner = id?.toIntOrNull() ?: 0
                )
            )
        }
    }
}