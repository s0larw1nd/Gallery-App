package pack.gallery

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
import org.w3c.dom.Text
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

        val imageRes = intent.getIntExtra("image", 0)
        val description = intent.getStringExtra("description")

        imageView.setImageResource(imageRes)
        textView.text = description

        shareBtn.setOnClickListener {
            val imageFile = File(this.cacheDir, "shared_image.png")
            val bitmap = BitmapFactory.decodeResource(this.resources, imageRes)
            FileOutputStream(imageFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 30, out)
            }

            val imageUri: Uri = FileProvider.getUriForFile(
                this,
                "${this.packageName}.fileprovider",
                imageFile
            )

            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, imageUri)
                putExtra(Intent.EXTRA_TEXT, description)
                type = "image/*"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            this.startActivity(Intent.createChooser(shareIntent, "Поделиться изображением"))
        }
    }
}