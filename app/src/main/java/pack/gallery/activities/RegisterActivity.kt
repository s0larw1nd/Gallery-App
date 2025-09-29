package pack.gallery.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pack.gallery.R
import pack.gallery.entities.User
import pack.gallery.providers.UserDatabaseProvider

class RegisterActivity : AppCompatActivity() {
    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val regBtn = findViewById<Button>(R.id.button)
        val logBtn = findViewById<Button>(R.id.button2)
        val tvUserName = findViewById<EditText>(R.id.etUsername)
        val tvPassword = findViewById<EditText>(R.id.etPassword)

        val db = UserDatabaseProvider.getDatabase(this)
        val userDao = db.userDao()

        regBtn.setOnClickListener {
            val username = tvUserName.getText().toString()
            val password = tvPassword.getText().toString().hashCode().toString()

            lifecycleScope.launch {
                userDao.insertUser(User(username = username, password = password))
                val id = userDao.getUserByUsername(username)?.id
                val sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("user_id", id.toString())
                editor.apply()
                startActivity(Intent(this@RegisterActivity, FeedActivity::class.java))
            }
        }

        logBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}