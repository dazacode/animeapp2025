package com.example.animeapp.custom

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Shader
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class GradientImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        isAntiAlias = true
    }

    init {
        setLayerType(LAYER_TYPE_HARDWARE, null)
        paint.isFilterBitmap = false
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Create the gradient shader
        val shader = LinearGradient(
            0f, 0f, 0f, height.toFloat(),
            intArrayOf(
                Color.TRANSPARENT,
                Color.BLACK.withOpacity(0.2f),
                Color.BLACK.withOpacity(0.2f),
                Color.TRANSPARENT
            ),
            floatArrayOf(0f, 0.3f, 0.7f, 1f),
            Shader.TileMode.CLAMP
        )

        // Apply the shader to the paint
        paint.shader = shader
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)

        // Draw the gradient overlay
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
    }

    // Helper function to apply opacity to color
    private fun Int.withOpacity(opacity: Float): Int {
        return Color.argb((opacity * 255).toInt(), Color.red(this), Color.green(this), Color.blue(this))
    }
}