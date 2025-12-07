package pack.gallery.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.compose.ui.util.fastCoerceAtLeast
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.slider.Slider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pack.gallery.R
import pack.gallery.factories.ImageViewModelFactory
import pack.gallery.providers.ImageDatabaseProvider
import pack.gallery.repositories.ImageRepository
import pack.gallery.repositories.PreferencesRepository
import pack.gallery.viewmodels.ImageViewModel
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toBitmap
import pack.gallery.views.CropOverlayView
import java.io.File
import java.io.FileOutputStream
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.sign


class EditActivity : AppCompatActivity() {
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

    data class DynamicButton(
        val text: String,
        val action: () -> Unit
    )

    data class Operation(
        val name: String,
        val arg: MutableMap<String, Float>,
        val action: (Float, Map<String, Float>) -> Float
    )

    private fun showButtons(buttons: List<DynamicButton>) {
        val container = findViewById<LinearLayout>(R.id.LinearLayoutTop)
        container.removeAllViews()

        val styledContext = ContextThemeWrapper(this@EditActivity, android.R.attr.buttonBarButtonStyle)

        for (b in buttons) {
            val button = Button(
                ContextThemeWrapper(this, android.R.attr.buttonBarButtonStyle),
                null,
                android.R.attr.buttonBarButtonStyle
            ).apply {
                this.text = b.text
                setOnClickListener { b.action() }
            }

            container.addView(button)
        }
    }

    fun crop(imageView: ImageView, overlay: CropOverlayView): Bitmap? {
        val drawable = imageView.drawable as? BitmapDrawable ?: return null
        val bitmap = drawable.bitmap ?: return null

        val overlayRect = RectF(overlay.cropRect)

        val locOverlay = IntArray(2).also { overlay.getLocationOnScreen(it) }
        val locImage = IntArray(2).also { imageView.getLocationOnScreen(it) }

        val offsetX = locOverlay[0] - locImage[0]
        val offsetY = locOverlay[1] - locImage[1]
        overlayRect.offset(offsetX.toFloat(), offsetY.toFloat())

        val inv = Matrix()
        if (!imageView.imageMatrix.invert(inv)) {
            return null
        }

        val bitmapRectF = RectF()
        inv.mapRect(bitmapRectF, overlayRect)

        val leftF = bitmapRectF.left.coerceAtLeast(0f)
        val topF = bitmapRectF.top.coerceAtLeast(0f)
        val rightF = bitmapRectF.right.coerceAtMost(bitmap.width.toFloat())
        val bottomF = bitmapRectF.bottom.coerceAtMost(bitmap.height.toFloat())

        var x = floor(leftF).toInt()
        var y = floor(topF).toInt()
        var w = max(1, ceil(rightF).toInt() - x)
        var h = max(1, ceil(bottomF).toInt() - y)

        if (x + w > bitmap.width) w = bitmap.width - x
        if (y + h > bitmap.height) h = bitmap.height - y
        if (w <= 0 || h <= 0) return null

        return try {
            Bitmap.createBitmap(bitmap, x, y, w, h)
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    private val handler = Handler(Looper.getMainLooper())
    private var debounceRunnable: Runnable? = null
    private val debounceDelay = 300L
    private val history = ArrayList<Operation>()
    private var cropping = false
    private val deleted_history = ArrayList<Operation>()

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val sliderSpace = findViewById<LinearLayout>(R.id.LinearLayoutSlider)
        val imageView = findViewById<ImageView>(R.id.imageView)
        val cropOverlay = findViewById<CropOverlayView>(R.id.cropOverlay)
        val levelsBtn = findViewById<Button>(R.id.LevelsBtn)
        val cropeBtn = findViewById<Button>(R.id.CutBtn)

        val undoBtn = findViewById<Button>(R.id.UndoBtn)
        val redoBtn = findViewById<Button>(R.id.RedoBtn)
        val saveBtn = findViewById<Button>(R.id.SaveBtn)
        val closeBtn = findViewById<Button>(R.id.CloseBtn)

        val imageId = intent.getIntExtra("id", 0)

        lifecycleScope.launch {
            val image = withContext(Dispatchers.IO) { viewModel.searchID(imageId) }
            val bitmap_fullsize = withContext(Dispatchers.IO) { BitmapFactory.decodeFile(image.filePath) }
            val bitmap = Bitmap.createScaledBitmap(bitmap_fullsize, 400, (400f / bitmap_fullsize.width * bitmap_fullsize.height).toInt(), true)

            imageView.setImageBitmap(bitmap)

            val w = bitmap.width
            val h = bitmap.height
            val originalPixels = IntArray(w * h)
            bitmap.getPixels(originalPixels, 0, w, 0, 0, w, h)

            val pixels = IntArray(w * h)
            val mutableBitmap = createBitmap(w, h)

            var value_brightness = 0.5f
            var value_contrast = 0.5f
            var value_saturation = 0.5f

            levelsBtn.setOnClickListener {
                showButtons(
                    listOf(
                        DynamicButton("Яркость") {
                            sliderSpace.removeAllViews()
                            val slider = Slider(this@EditActivity)
                            slider.value = value_brightness
                            slider.stepSize = 0.01f

                            slider.addOnChangeListener { _, value, _ ->
                                debounceRunnable?.let { handler.removeCallbacks(it) }

                                debounceRunnable = Runnable {
                                    value_brightness = value
                                    undoBtn.isEnabled = true
                                    redoBtn.isEnabled = false

                                    if (history.isEmpty() || history.last().name != "Яркость") {
                                        history.add(
                                            Operation(
                                                name = "Яркость",
                                                arg = mutableMapOf("brightness" to (value - 0.5f) * 200f),
                                                action = { x, arg ->
                                                    val brightness = arg["brightness"] ?: 0f
                                                    x + brightness
                                                }
                                            ))
                                    }
                                    else {
                                        history.removeLast()
                                        history.add(
                                            Operation(
                                                name = "Яркость",
                                                arg = mutableMapOf("brightness" to (value - 0.5f) * 200f),
                                                action = { x, arg ->
                                                    val brightness = arg["brightness"] ?: 0f
                                                    x + brightness
                                                }
                                            ))
                                    }
                                    deleted_history.clear()

                                    for (i in originalPixels.indices) {
                                        val p = originalPixels[i]

                                        val a = p ushr 24 and 0xFF
                                        var r = (p ushr 16 and 0xFF).toFloat()
                                        var g = (p ushr 8 and 0xFF).toFloat()
                                        var b = (p and 0xFF).toFloat()

                                        history.forEach {
                                            val gray = (0.299 * r + 0.587 * g + 0.114 * b).toFloat()

                                            it.arg["gray"] = gray
                                            r = it.action(r, it.arg).coerceIn(0f, 255f)
                                            g = it.action(g, it.arg).coerceIn(0f, 255f)
                                            b = it.action(b, it.arg).coerceIn(0f, 255f)
                                        }

                                        val nr = r.toInt().coerceIn(0, 255)
                                        val ng = g.toInt().coerceIn(0, 255)
                                        val nb = b.toInt().coerceIn(0, 255)

                                        pixels[i] = (a shl 24) or (nr shl 16) or (ng shl 8) or nb
                                    }

                                    mutableBitmap.setPixels(pixels, 0, w, 0, 0, w, h)
                                    imageView.setImageBitmap(mutableBitmap)
                                }

                                handler.postDelayed(debounceRunnable!!, debounceDelay)
                            }

                            sliderSpace.addView(slider)
                        },

                        DynamicButton("Контрастность") {
                            sliderSpace.removeAllViews()
                            val slider = Slider(this@EditActivity)
                            slider.value = value_contrast
                            slider.stepSize = 0.01f

                            slider.addOnChangeListener { _, value, _ ->
                                debounceRunnable?.let { handler.removeCallbacks(it) }

                                debounceRunnable = Runnable {
                                    value_contrast = value
                                    undoBtn.isEnabled = true
                                    redoBtn.isEnabled = false

                                    if (history.isEmpty() || history.last().name != "Контрастность") {
                                        history.add(
                                            Operation(
                                            name = "Контрастность",
                                            arg = mutableMapOf("contrast" to value * 2),
                                            action = { x, arg ->
                                                val contrast = arg["contrast"] ?: 1f
                                                (x - 128) * contrast + 128
                                            }
                                        ))
                                    } else {
                                        history.removeLast()
                                        history.add(
                                            Operation(
                                            name = "Контрастность",
                                            arg = mutableMapOf("contrast" to value * 2),
                                            action = { x, arg ->
                                                val contrast = arg["contrast"] ?: 1f
                                                (x - 128) * contrast + 128
                                            }
                                        ))
                                    }
                                    deleted_history.clear()

                                    for (i in originalPixels.indices) {
                                        val p = originalPixels[i]

                                        val a = p ushr 24 and 0xFF
                                        var r = (p ushr 16 and 0xFF).toFloat()
                                        var g = (p ushr 8 and 0xFF).toFloat()
                                        var b = (p and 0xFF).toFloat()

                                        history.forEach {
                                            val gray = (0.299 * r + 0.587 * g + 0.114 * b).toFloat()

                                            it.arg["gray"] = gray
                                            r = it.action(r, it.arg).coerceIn(0f, 255f)
                                            g = it.action(g, it.arg).coerceIn(0f, 255f)
                                            b = it.action(b, it.arg).coerceIn(0f, 255f)
                                        }

                                        val nr = r.toInt().coerceIn(0, 255)
                                        val ng = g.toInt().coerceIn(0, 255)
                                        val nb = b.toInt().coerceIn(0, 255)

                                        pixels[i] = (a shl 24) or (nr shl 16) or (ng shl 8) or nb
                                    }

                                    mutableBitmap.setPixels(pixels, 0, w, 0, 0, w, h)
                                    imageView.setImageBitmap(mutableBitmap)
                                }

                                handler.postDelayed(debounceRunnable!!, debounceDelay)
                            }

                            sliderSpace.addView(slider)
                        },

                        DynamicButton("Насыщенность") {
                            sliderSpace.removeAllViews()
                            val slider = Slider(this@EditActivity)
                            slider.value = value_saturation
                            slider.stepSize = 0.01f

                            slider.addOnChangeListener { _, value, _ ->
                                debounceRunnable?.let { handler.removeCallbacks(it) }

                                debounceRunnable = Runnable {
                                    value_saturation = value
                                    undoBtn.isEnabled = true
                                    redoBtn.isEnabled = false

                                    if (history.isEmpty() || history.last().name != "Насыщенность") {
                                        history.add(
                                            Operation(
                                            name = "Насыщенность",
                                            arg = mutableMapOf("s" to value * 2),
                                            action = { x, arg, ->
                                                val s = arg["s"] ?: 1f
                                                val gray = arg["gray"] ?: 0f
                                                gray + s * (x - gray)
                                            }
                                        ))
                                    } else {
                                        history.removeLast()
                                        history.add(
                                            Operation(
                                            name = "Насыщенность",
                                            arg = mutableMapOf("s" to value * 2),
                                            action = { x, arg, ->
                                                val s = arg["s"] ?: 1f
                                                val gray = arg["gray"] ?: 0f
                                                gray + s * (x - gray)
                                            }
                                        ))
                                    }
                                    deleted_history.clear()

                                    for (i in originalPixels.indices) {
                                        val p = originalPixels[i]

                                        val a = p ushr 24 and 0xFF
                                        var r = (p ushr 16 and 0xFF).toFloat()
                                        var g = (p ushr 8 and 0xFF).toFloat()
                                        var b = (p and 0xFF).toFloat()

                                        history.forEach {
                                            val gray = (0.299 * r + 0.587 * g + 0.114 * b).toFloat()

                                            it.arg["gray"] = gray
                                            r = it.action(r, it.arg).coerceIn(0f, 255f)
                                            g = it.action(g, it.arg).coerceIn(0f, 255f)
                                            b = it.action(b, it.arg).coerceIn(0f, 255f)
                                        }

                                        val nr = r.toInt().coerceIn(0, 255)
                                        val ng = g.toInt().coerceIn(0, 255)
                                        val nb = b.toInt().coerceIn(0, 255)

                                        pixels[i] = (a shl 24) or (nr shl 16) or (ng shl 8) or nb
                                    }

                                    mutableBitmap.setPixels(pixels, 0, w, 0, 0, w, h)
                                    imageView.setImageBitmap(mutableBitmap)
                                }

                                handler.postDelayed(debounceRunnable!!, debounceDelay)
                            }

                            sliderSpace.addView(slider)
                        }
                    )
                )
            }

            undoBtn.setOnClickListener {
                if (!history.isEmpty()) {
                    var last = history.removeLast()
                    if (history.isEmpty()) undoBtn.isEnabled = false
                    deleted_history.add(last)
                    redoBtn.isEnabled = true

                    for (i in originalPixels.indices) {
                        val p = originalPixels[i]

                        val a = p ushr 24 and 0xFF
                        var r = (p ushr 16 and 0xFF).toFloat()
                        var g = (p ushr 8 and 0xFF).toFloat()
                        var b = (p and 0xFF).toFloat()

                        history.forEach {
                            val gray = (0.299 * r + 0.587 * g + 0.114 * b).toFloat()

                            it.arg["gray"] = gray
                            r = it.action(r, it.arg).coerceIn(0f, 255f)
                            g = it.action(g, it.arg).coerceIn(0f, 255f)
                            b = it.action(b, it.arg).coerceIn(0f, 255f)
                        }

                        val nr = r.toInt().coerceIn(0, 255)
                        val ng = g.toInt().coerceIn(0, 255)
                        val nb = b.toInt().coerceIn(0, 255)

                        pixels[i] = (a shl 24) or (nr shl 16) or (ng shl 8) or nb
                    }

                    mutableBitmap.setPixels(pixels, 0, w, 0, 0, w, h)
                    imageView.setImageBitmap(mutableBitmap)
                }
            }

            redoBtn.setOnClickListener {
                if (!deleted_history.isEmpty()) {
                    var deleted_last = deleted_history.removeFirst()
                    if (deleted_history.isEmpty()) redoBtn.isEnabled = false
                    history.add(deleted_last)
                    undoBtn.isEnabled = true

                    for (i in originalPixels.indices) {
                        val p = originalPixels[i]

                        val a = p ushr 24 and 0xFF
                        var r = (p ushr 16 and 0xFF).toFloat()
                        var g = (p ushr 8 and 0xFF).toFloat()
                        var b = (p and 0xFF).toFloat()

                        history.forEach {
                            val gray = (0.299 * r + 0.587 * g + 0.114 * b).toFloat()

                            it.arg["gray"] = gray
                            r = it.action(r, it.arg).coerceIn(0f, 255f)
                            g = it.action(g, it.arg).coerceIn(0f, 255f)
                            b = it.action(b, it.arg).coerceIn(0f, 255f)
                        }

                        val nr = r.toInt().coerceIn(0, 255)
                        val ng = g.toInt().coerceIn(0, 255)
                        val nb = b.toInt().coerceIn(0, 255)

                        pixels[i] = (a shl 24) or (nr shl 16) or (ng shl 8) or nb
                    }

                    mutableBitmap.setPixels(pixels, 0, w, 0, 0, w, h)
                    imageView.setImageBitmap(mutableBitmap)
                }
            }

            cropeBtn.setOnClickListener {
                if (!cropping) {
                    cropOverlay.visibility = View.VISIBLE
                    cropping = true
                }
                else{
                    Log.i("CROP", crop(imageView, cropOverlay).toString())
                    imageView.setImageBitmap(crop(imageView, cropOverlay))
                    cropOverlay.visibility = View.GONE
                    cropping = false
                }
            }

            closeBtn.setOnClickListener {
                val intent = Intent(this@EditActivity, ImageActivity::class.java)
                intent.putExtra("id", imageId)
                startActivity(intent)
                finish()
            }

            saveBtn.setOnClickListener {
                lifecycleScope.launch {
                    val file = File(this@EditActivity.filesDir, "photo_${System.currentTimeMillis()}.jpg")
                    val bs: Bitmap = imageView.drawable.toBitmap()

                    FileOutputStream(file).use { out ->
                        bs.compress(Bitmap.CompressFormat.JPEG, 100, out)
                    }

                    viewModel.updateFilepathId(imageId, file.absolutePath)
                    val intent = Intent(this@EditActivity, ImageActivity::class.java)
                    intent.putExtra("id", imageId)
                    startActivity(intent)
                    finish()
                }
            }
        }
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