package pack.gallery.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.compose.ui.text.intl.Locale
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pack.gallery.R
import pack.gallery.providers.UserDatabaseProvider
import androidx.core.content.edit

class SettingsActivity : AppCompatActivity() {
    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val logoutBtn = findViewById<Button>(R.id.button)
        val langBtn = findViewById<Button>(R.id.button2)
        val themeBtn = findViewById<Button>(R.id.button4)

        val db = UserDatabaseProvider.getDatabase(this)
        val userDao = db.userDao()

        val sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)

        lifecycleScope.launch {
            val id = sharedPreferences.getString("user_id", "NULL")
            val user = userDao.getUserById(id.toString())
            findViewById<TextView>(R.id.LoginInfo).setText(user?.username)
        }

        logoutBtn.setOnClickListener {
            sharedPreferences.edit(commit = true) {
                putString("user_id", "NULL")
            }

            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }

        langBtn.setOnClickListener {
            val appLocales = AppCompatDelegate.getApplicationLocales()
            val currentLocale = appLocales[0]
            val languageCode = currentLocale?.language

            if (languageCode == "NULL" || languageCode == "ru") {
                val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags("en")
                AppCompatDelegate.setApplicationLocales(appLocale)
            }
            else {
                val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags("ru")
                AppCompatDelegate.setApplicationLocales(appLocale)
            }
        }

        themeBtn.setOnClickListener {
            val theme = sharedPreferences.getString("theme", "NULL")
            if (theme == "DAY" || theme == "NULL") {
                sharedPreferences.edit(commit = true) {
                    putString("theme", "NIGHT")
                }
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            else {
                sharedPreferences.edit(commit = true) {
                    putString("theme", "DAY")
                }
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    @Override
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    @Override
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_home -> {
                startActivity(Intent(this, FeedActivity::class.java))
                return true
            }

            R.id.action_album -> {
                startActivity(Intent(this, GalleryActivity::class.java))
                return true
            }

            R.id.action_about -> {
                startActivity(Intent(this, InfoActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}