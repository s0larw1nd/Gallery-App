package pack.gallery

import android.content.Intent
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
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import pack.gallery.ui.theme.GalleryTheme

class MainActivity : AppCompatActivity() {
    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val data = listOf(
            ImageModel(R.drawable.image, "описание 1"),
            ImageModel(R.drawable.image, "описание 2"),
            ImageModel(R.drawable.image, "описание 2"),
            ImageModel(R.drawable.image, "описание 2"),
            ImageModel(R.drawable.image, "описание 2"),
            ImageModel(R.drawable.image, "описание 2"),
            ImageModel(R.drawable.image, "описание 2"),
            ImageModel(R.drawable.image, "описание 2"),
            ImageModel(R.drawable.image, "описание 2"),
            ImageModel(R.drawable.image, "описание 2"),
            ImageModel(R.drawable.image, "описание 2"),
            ImageModel(R.drawable.image, "описание 2"),
            ImageModel(R.drawable.image, "описание 2"),
            ImageModel(R.drawable.image, "описание 2"),
            ImageModel(R.drawable.image, "описание 2"),
            ImageModel(R.drawable.image, "описание 2"),
            ImageModel(R.drawable.image, "описание 2"),
            ImageModel(R.drawable.image, "описание 2"),
            ImageModel(R.drawable.image, "описание 2"),
            ImageModel(R.drawable.image, "описание 2"),
            ImageModel(R.drawable.image, "описание 2"),
            ImageModel(R.drawable.image, "описание 2"),
            ImageModel(R.drawable.image, "описание 2"),
            ImageModel(R.drawable.image, "описание 2"),
            ImageModel(R.drawable.image, "описание 2"),
            ImageModel(R.drawable.image, "описание 2"),
            ImageModel(R.drawable.image, "описание 2"),
            ImageModel(R.drawable.image, "описание 2")
        )

        val recycler = findViewById<RecyclerView>(R.id.recyclerView)
        val layoutManager = StaggeredGridLayoutManager(
            4,
            StaggeredGridLayoutManager.VERTICAL
        )
        recycler.layoutManager = layoutManager

        recycler.adapter = ImageAdapter(data)
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
                Intent(this, SettingsActivity::class.java).also {
                    ContextCompat.startActivity(this, it, null)
                }
                true
            }
            R.id.action_about -> {
                Intent(this, InfoActivity::class.java).also {
                    ContextCompat.startActivity(this, it, null)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}