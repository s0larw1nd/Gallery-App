package pack.gallery.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import pack.gallery.R
import pack.gallery.entities.User
import pack.gallery.providers.UserDatabaseProvider
import androidx.core.content.edit

class RegisterActivity : AppCompatActivity() {
    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val regBtn = findViewById<Button>(R.id.button)
        val logBtn = findViewById<Button>(R.id.button2)

        val usernameInputLayout = findViewById<TextInputLayout>(R.id.tilUsername)
        val usernameEditText = findViewById<TextInputEditText>(R.id.etUsername)
        val passwordInputLayout = findViewById<TextInputLayout>(R.id.tilPassword)
        val passwordEditText = findViewById<TextInputEditText>(R.id.etPassword)

        val db = UserDatabaseProvider.getDatabase(this)
        val userDao = db.userDao()

        regBtn.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (validateUsername(usernameInputLayout, username) &&
                validatePassword(passwordInputLayout, password)) {
                lifecycleScope.launch {
                    val result = userDao.insertUser(User(username = username, password = password))
                    if (result == -1L) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Имя пользователя занято",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else {
                        val id = userDao.getUserByUsername(username)?.id
                        val sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
                        sharedPreferences.edit(commit = true) {
                            putString("user_id", id.toString())
                        }
                        startActivity(Intent(this@RegisterActivity, FeedActivity::class.java))
                        finish()
                    }
                }
            }
        }

        logBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}