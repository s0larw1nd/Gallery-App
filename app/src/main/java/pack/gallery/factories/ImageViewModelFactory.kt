package pack.gallery.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import pack.gallery.repositories.ImageRepository
import pack.gallery.repositories.PreferencesRepository
import pack.gallery.viewmodels.ImageViewModel

class ImageViewModelFactory(
    private val imageRepository: ImageRepository,
    private val prefsRepository: PreferencesRepository
) : ViewModelProvider.Factory {

    @Override
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ImageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ImageViewModel(imageRepository, prefsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}