package pack.gallery.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.net.Uri
import android.view.MenuItem
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.launch
import pack.gallery.activities.GalleryActivity
import pack.gallery.providers.ImageDatabaseProvider
import pack.gallery.activities.InfoActivity
import pack.gallery.R
import pack.gallery.activities.SettingsActivity
import pack.gallery.adapters.ViewPagerAdapter
import pack.gallery.entities.Image
import pack.gallery.providers.UserDatabaseProvider
import java.io.File
import java.io.FileOutputStream

class FeedActivity : AppCompatActivity() {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter
        viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL

        val db = ImageDatabaseProvider.getDatabase(this)
        val imageDao = db.imageDao()

        lifecycleScope.launch {
            val images = imageDao.getAll()
            adapter.submitImages(images)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add -> {
                pickImage.launch("image/*")
            }

            R.id.action_album -> {
                startActivity(Intent(this, GalleryActivity::class.java))
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