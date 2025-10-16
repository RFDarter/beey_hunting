package com.rfdarter.beehunting.beelogging.ui.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class CircularImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : AppCompatImageView(context, attrs, defStyle) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }
    private val shaderMatrix = Matrix()
    private var bitmapShader: BitmapShader? = null
    private var bitmap: Bitmap? = null
    private var radius = 0f
    private var strokeWidth = dpToPx(2)

    init {
        scaleType = ScaleType.CENTER_CROP
        strokePaint.color = Color.WHITE
        strokePaint.strokeWidth = strokeWidth.toFloat()
    }

    fun setStroke(color: Int, widthDp: Int) {
        strokePaint.color = color
        strokeWidth = dpToPx(widthDp)
        strokePaint.strokeWidth = strokeWidth.toFloat()
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rebuildShader()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        rebuildShader()
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        rebuildShader()
    }

    private fun rebuildShader() {
        bitmap = drawableToBitmap(drawable)
        bitmap?.let {
            bitmapShader = BitmapShader(it, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            paint.shader = bitmapShader
            updateShaderMatrix()
        } ?: run {
            bitmapShader = null
            paint.shader = null
        }
        invalidate()
    }

    private fun updateShaderMatrix() {
        val bm = bitmap ?: return
        val viewWidth = width - paddingLeft - paddingRight
        val viewHeight = height - paddingTop - paddingBottom
        if (viewWidth <= 0 || viewHeight <= 0) return

        val scale: Float
        val dx: Float
        val dy: Float

        val bmWidth = bm.width.toFloat()
        val bmHeight = bm.height.toFloat()

        // centerCrop logic
        if (bmWidth * viewHeight > viewWidth * bmHeight) {
            scale = viewHeight / bmHeight
            dx = (viewWidth - bmWidth * scale) * 0.5f
            dy = 0f
        } else {
            scale = viewWidth / bmWidth
            dx = 0f
            dy = (viewHeight - bmHeight * scale) * 0.5f
        }

        shaderMatrix.setScale(scale, scale)
        shaderMatrix.postTranslate(dx + paddingLeft, dy + paddingTop)
        bitmapShader?.setLocalMatrix(shaderMatrix)

        val minDim = Math.min(viewWidth, viewHeight)
        radius = minDim * 0.5f
    }

    override fun onDraw(canvas: Canvas) {
        val drawable = drawable
        if (drawable == null || width == 0 || height == 0) {
            return
        }

        val cx = width * 0.5f
        val cy = height * 0.5f
        val r = radius

        // draw image circle
        canvas.drawCircle(cx, cy, r, paint)

        // draw stroke if width > 0
        if (strokeWidth > 0) {
            val strokeRadius = r - strokeWidth * 0.5f
            if (strokeRadius > 0f) {
                canvas.drawCircle(cx, cy, strokeRadius, strokePaint)
            }
        }
    }

    private fun drawableToBitmap(drawable: Drawable?): Bitmap? {
        if (drawable == null) return null
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        val w = drawable.intrinsicWidth.coerceAtLeast(1)
        val h = drawable.intrinsicHeight.coerceAtLeast(1)
        return try {
            val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bmp)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bmp
        } catch (e: Exception) {
            null
        }
    }

    private fun dpToPx(dp: Int): Int =
        (dp * resources.displayMetrics.density + 0.5f).toInt()
}