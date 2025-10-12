package pack.gallery.activities

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pack.gallery.R
import pack.gallery.providers.UserDatabaseProvider
import androidx.core.content.edit

class LoginActivity : AppCompatActivity() {
    var isReady = false
    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        splashScreen.setKeepOnScreenCondition { !isReady }
        lifecycleScope.launch {
            delay(3000)
            isReady = true
        }

        setContentView(R.layout.activity_login)

        val sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
        val id = sharedPreferences.getString("user_id", "NULL")

        var theme = sharedPreferences.getString("theme", "NULL")
        if (theme == "NULL") {
            val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            when (currentNightMode) {
                Configuration.UI_MODE_NIGHT_NO -> {
                    theme = "DAY"
                }
                Configuration.UI_MODE_NIGHT_YES -> {
                    theme = "NIGHT"
                }
                Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                    theme = "DAY"
                }
            }
            sharedPreferences.edit(commit = true) {
                putString("theme", theme)
            }
        }

        if (id != "NULL") startActivity(Intent(this, FeedActivity::class.java))

        val logBtn = findViewById<Button>(R.id.button)
        val regBtn = findViewById<Button>(R.id.button2)

        val usernameInputLayout = findViewById<TextInputLayout>(R.id.tilUsername)
        val usernameEditText = findViewById<TextInputEditText>(R.id.etUsername)
        val passwordInputLayout = findViewById<TextInputLayout>(R.id.tilPassword)
        val passwordEditText = findViewById<TextInputEditText>(R.id.etPassword)

        val db = UserDatabaseProvider.getDatabase(this)
        val userDao = db.userDao()

        logBtn.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (validateUsername(usernameInputLayout, username) &&
                validatePassword(passwordInputLayout, password)) {
                lifecycleScope.launch {
                    val user = userDao.getUserByUsername(username)

                    if (user != null && user.password == password) {
                        val sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
                        sharedPreferences.edit(commit = true) {
                            putString("user_id", user.id.toString())
                        }
                        startActivity(Intent(this@LoginActivity, FeedActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Неверное имя пользователя или пароль",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        regBtn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}