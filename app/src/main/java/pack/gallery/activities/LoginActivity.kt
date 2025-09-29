package pack.gallery.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pack.gallery.R
import pack.gallery.entities.User
import pack.gallery.providers.UserDatabaseProvider

class LoginActivity : AppCompatActivity() {
    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
        val id = sharedPreferences.getString("user_id", "NULL")

        if (id != "NULL") startActivity(Intent(this, FeedActivity::class.java))

        val logBtn = findViewById<Button>(R.id.button)
        val regBtn = findViewById<Button>(R.id.button2)
        val tvUserName = findViewById<EditText>(R.id.etUsername)
        val tvPassword = findViewById<EditText>(R.id.etPassword)

        val db = UserDatabaseProvider.getDatabase(this)
        val userDao = db.userDao()

        logBtn.setOnClickListener {
            val username = tvUserName.getText().toString()
            val password = tvPassword.getText().toString().hashCode().toString()

            lifecycleScope.launch {
                val user = userDao.getUserByUsername(username)

                if (user != null && user.password == password) {
                    val sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
                    with(sharedPreferences.edit()) {
                        putString("user_id", user.id.toString())
                        apply()
                    }

                    startActivity(Intent(this@LoginActivity, FeedActivity::class.java))
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Неверное имя пользователя или пароль",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        regBtn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}