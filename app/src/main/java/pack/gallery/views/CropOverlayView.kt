package pack.gallery.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

class CropOverlayView(context: Context, attrs: AttributeSet? = null)
    : View(context, attrs) {

    private val borderPaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 6f
        isAntiAlias = true
    }

    private val touchMargin = 40f

    enum class Mode { NONE, MOVE, RESIZE_LEFT, RESIZE_RIGHT, RESIZE_TOP, RESIZE_BOTTOM, RESIZE_LT, RESIZE_RT, RESIZE_LB, RESIZE_RB }

    private var mode = Mode.NONE
    var cropRect = RectF(200f, 200f, 800f, 800f)

    private var lastX = 0f
    private var lastY = 0f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val layerId = canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)

        val dimPaint = Paint().apply {
            color = Color.parseColor("#88000000")
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), dimPaint)

        val clearPaint = Paint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        }
        canvas.drawRect(cropRect, clearPaint)

        clearPaint.xfermode = null

        canvas.restoreToCount(layerId)

        canvas.drawRect(cropRect, borderPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {

            MotionEvent.ACTION_DOWN -> {
                mode = detectTouchMode(x, y)
                lastX = x
                lastY = y
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                val dx = x - lastX
                val dy = y - lastY

                when (mode) {
                    Mode.MOVE -> cropRect.offset(dx, dy)

                    Mode.RESIZE_LEFT   -> cropRect.left += dx
                    Mode.RESIZE_RIGHT  -> cropRect.right += dx
                    Mode.RESIZE_TOP    -> cropRect.top += dy
                    Mode.RESIZE_BOTTOM -> cropRect.bottom += dy

                    Mode.RESIZE_LT -> { cropRect.left += dx;  cropRect.top += dy }
                    Mode.RESIZE_RT -> { cropRect.right += dx; cropRect.top += dy }
                    Mode.RESIZE_LB -> { cropRect.left += dx;  cropRect.bottom += dy }
                    Mode.RESIZE_RB -> { cropRect.right += dx; cropRect.bottom += dy }

                    else -> {}
                }

                fixMinSize()
                invalidate()

                lastX = x
                lastY = y
                return true
            }

            MotionEvent.ACTION_UP -> {
                mode = Mode.NONE
            }
        }

        return super.onTouchEvent(event)
    }

    private fun detectTouchMode(x: Float, y: Float): Mode {

        val left = abs(x - cropRect.left) < touchMargin
        val right = abs(x - cropRect.right) < touchMargin
        val top = abs(y - cropRect.top) < touchMargin
        val bottom = abs(y - cropRect.bottom) < touchMargin
        val inside = cropRect.contains(x, y)

        return when {
            left && top -> Mode.RESIZE_LT
            right && top -> Mode.RESIZE_RT
            left && bottom -> Mode.RESIZE_LB
            right && bottom -> Mode.RESIZE_RB

            left -> Mode.RESIZE_LEFT
            right -> Mode.RESIZE_RIGHT
            top -> Mode.RESIZE_TOP
            bottom -> Mode.RESIZE_BOTTOM

            inside -> Mode.MOVE

            else -> Mode.NONE
        }
    }

    private fun fixMinSize() {
        val minSize = 100f
        if (cropRect.width() < minSize) cropRect.right = cropRect.left + minSize
        if (cropRect.height() < minSize) cropRect.bottom = cropRect.top + minSize
    }
}
