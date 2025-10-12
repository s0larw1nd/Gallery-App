package pack.gallery

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class GalleryApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
        var theme = sharedPreferences.getString("theme", "NULL")

        AppCompatDelegate.setDefaultNightMode(
            if (theme=="NIGHT") AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}