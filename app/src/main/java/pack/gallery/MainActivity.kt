package pack.gallery

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import pack.gallery.ui.theme.GalleryTheme

class MainActivity : ComponentActivity() {
    private lateinit var btn: Button
    private lateinit var img: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn = findViewById(R.id.button)
        img = findViewById(R.id.imageView)

        val descripts = listOf("Picture 1")

        //btn.setOnClickListener { Toast.makeText(this, "Привет, мир!", Toast.LENGTH_SHORT).show() }
        img.setOnClickListener { Toast.makeText(this, descripts[0], Toast.LENGTH_SHORT).show() }
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