package pack.gallery.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.transition.TransitionInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pack.gallery.R
import pack.gallery.factories.ImageViewModelFactory
import pack.gallery.providers.ImageDatabaseProvider
import pack.gallery.repositories.ImageRepository
import pack.gallery.repositories.PreferencesRepository
import pack.gallery.viewmodels.ImageViewModel
import java.io.File
import java.io.FileOutputStream
import kotlin.getValue

class ImageActivity : AppCompatActivity() {
    private val imageRepository: ImageRepository by lazy {
        val db = ImageDatabaseProvider.getDatabase(this)
        ImageRepository(db.imageDao())
    }
    private val prefsRepository: PreferencesRepository by lazy {
        PreferencesRepository(applicationContext)
    }
    private val viewModel: ImageViewModel by viewModels {
        ImageViewModelFactory(imageRepository,prefsRepository)
    }
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            viewModel.saveImageToInternalStorage(it, contentResolver, filesDir)
        }
    }

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        window.sharedElementEnterTransition = TransitionInflater
            .from(this)
            .inflateTransition(android.R.transition.move)

        val imageView = findViewById<ImageView>(R.id.imageView)
        val textEdit = findViewById<TextView>(R.id.textEdit)
        val shareBtn = findViewById<Button>(R.id.button)
        val saveBtn = findViewById<Button>(R.id.button2)
        val delBtn = findViewById<Button>(R.id.button3)
        saveBtn.setEnabled(false)

        val imageId = intent.getIntExtra("id", 0)

        lifecycleScope.launch {
            val image = withContext(Dispatchers.IO) { viewModel.searchID(imageId) }
            val bitmap = withContext(Dispatchers.IO) { BitmapFactory.decodeFile(image.filePath) }

            imageView.setImageBitmap(bitmap)
            textEdit.text = image.description

            shareBtn.setOnClickListener {
                val imageFile = File(this@ImageActivity.cacheDir, "shared_image.png")
                val bitmap = BitmapFactory.decodeFile(image.filePath)
                FileOutputStream(imageFile).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 30, out)
                }

                val imageUri: Uri = FileProvider.getUriForFile(
                    this@ImageActivity,
                    "${this@ImageActivity.packageName}.fileprovider",
                    imageFile
                )

                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_STREAM, imageUri)
                    putExtra(Intent.EXTRA_TEXT, image.description)
                    type = "image/*"
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                this@ImageActivity.startActivity(
                    Intent.createChooser(
                        shareIntent,
                        "Поделиться изображением"
                    )
                )
            }
        }

        delBtn.setOnClickListener {
            lifecycleScope.launch {
                withContext(Dispatchers.IO) { viewModel.deleteId(imageId) }
                startActivity(Intent(this@ImageActivity, GalleryActivity::class.java))
                finish()
            }
        }

        saveBtn.setOnClickListener {
            lifecycleScope.launch {
                viewModel.updateDescId(
                        imageId,
                        textEdit.getText().toString()
                    )
                saveBtn.setEnabled(false)
            }
        }

        textEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                if (!textEdit.hasFocus()) {
                    return
                }
                saveBtn.setEnabled(true)
            }
        })
    }

    @Override
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
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