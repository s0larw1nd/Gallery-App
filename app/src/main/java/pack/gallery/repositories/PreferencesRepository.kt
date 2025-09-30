package pack.gallery.repositories

import android.content.Context

class PreferencesRepository(context: Context) {
    private val prefs = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)

    fun saveValue(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun getString(key: String): String? {
        return prefs.getString(key, "NULL")
    }
}