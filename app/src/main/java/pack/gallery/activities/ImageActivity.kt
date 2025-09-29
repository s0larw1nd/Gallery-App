package pack.gallery.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pack.gallery.R
import pack.gallery.adapters.ImageAdapter
import pack.gallery.providers.ImageDatabaseProvider
import java.io.File
import java.io.FileOutputStream

class ImageActivity : AppCompatActivity() {
    private lateinit var imageView : ImageView
    private lateinit var textView : TextView
    private lateinit var shareBtn : Button

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        val imageView = findViewById<ImageView>(R.id.imageView)
        val textView = findViewById<TextView>(R.id.textView)
        val shareBtn = findViewById<Button>(R.id.button)
        val intent = intent;

        val imageId = intent.getIntExtra("id", 0)

        val db = ImageDatabaseProvider.getDatabase(this)
        val imageDao = db.imageDao()

        lifecycleScope.launch {
            val image = imageDao.getID(imageId)

            val bitmap = BitmapFactory.decodeFile(image.filePath)
            imageView.setImageBitmap(bitmap)
            textView.text = image.description

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

                this@ImageActivity.startActivity(Intent.createChooser(shareIntent, "Поделиться изображением"))
            }
        }
    }
}