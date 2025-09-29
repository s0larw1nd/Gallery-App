package pack.gallery.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.coroutines.launch
import pack.gallery.adapters.ImageAdapter
import pack.gallery.models.ImageModel
import pack.gallery.activities.InfoActivity
import pack.gallery.R
import pack.gallery.activities.SettingsActivity
import pack.gallery.entities.Image
import pack.gallery.providers.ImageDatabaseProvider
import java.io.File
import java.io.FileOutputStream

class GalleryActivity : AppCompatActivity() {
    private fun saveImageToInternalStorage(uri: Uri): Unit {
        val inputStream = contentResolver.openInputStream(uri)
        val fileName = "photo_${System.currentTimeMillis()}.jpg"
        val file = File(filesDir, fileName)
        val outputStream = FileOutputStream(file)

        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()

        val sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
        val id = sharedPreferences.getString("user_id", "NULL")

        val db = ImageDatabaseProvider.getDatabase(this)
        val imageDao = db.imageDao()

        lifecycleScope.launch {
            imageDao.insert(
                Image(
                    filePath = file.absolutePath,
                    description = "",
                    owner = id?.toIntOrNull() ?: 0
                )
            )
        }
    }

    val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            saveImageToInternalStorage(it)
        }
    }

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val recycler = findViewById<RecyclerView>(R.id.recyclerView)
        val layoutManager = StaggeredGridLayoutManager(
            4,
            StaggeredGridLayoutManager.VERTICAL
        )
        recycler.layoutManager = layoutManager

        val db = ImageDatabaseProvider.getDatabase(this)
        val imageDao = db.imageDao()

        lifecycleScope.launch {
            val images = imageDao.getAll()
            recycler.adapter = ImageAdapter(images)
        }
    }

    @Override
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    @Override
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add -> {
                pickImage.launch("image/*")
            }

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
        }
        return super.onOptionsItemSelected(item)
    }
}