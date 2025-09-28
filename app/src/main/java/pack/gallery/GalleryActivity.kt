package pack.gallery

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class GalleryActivity : AppCompatActivity() {
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
            R.id.action_home -> {
                startActivity(Intent(this, FeedActivity::class.java))
                return true
            }

            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }

            R.id.action_about -> {
                startActivity(Intent(this, InfoActivity::class.java))
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}