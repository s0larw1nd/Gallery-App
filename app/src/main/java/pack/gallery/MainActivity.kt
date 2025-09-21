package pack.gallery

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import pack.gallery.ui.theme.GalleryTheme

class MainActivity : AppCompatActivity() {
    private lateinit var btn: Button
    private lateinit var img: ImageView

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        img = findViewById(R.id.imageView)

        val descripts = listOf("Picture 1")

        //btn.setOnClickListener { Toast.makeText(this, "Привет, мир!", Toast.LENGTH_SHORT).show() }
        img.setOnClickListener { Toast.makeText(this, descripts[0], Toast.LENGTH_SHORT).show() }
    }

    @Override
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    @Override
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                // Обработка нажатия "Настройки"
                true
            }
            R.id.action_about -> {
                // Обработка нажатия "О приложении"
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GalleryTheme {
        Greeting("Android")
    }
}