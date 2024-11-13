package com.kawaidev.kawaime.ui.custom

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

class VerticalGradientImage @JvmOverloads constructor(
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

        // Vertical gradient shader from top to bottom
        val verticalShader = LinearGradient(
            0f, 0f, 0f, height.toFloat(),
            intArrayOf(
                Color.BLACK.withOpacity(0.6f),
                Color.BLACK.withOpacity(0.5f),
                Color.BLACK.withOpacity(0.4f),
                Color.BLACK.withOpacity(0.3f),
                Color.BLACK.withOpacity(0.1f),
                Color.TRANSPARENT
            ),
            floatArrayOf(0f, 0.2f, 0.4f, 0.6f, 0.8f, 1f),
            Shader.TileMode.CLAMP
        )

        // Apply the vertical shader to the paint
        paint.shader = verticalShader
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)

        // Draw the gradient overlay
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
    }

    // Helper function to apply opacity to color
    private fun Int.withOpacity(opacity: Float): Int {
        return Color.argb((opacity * 255).toInt(), Color.red(this), Color.green(this), Color.blue(this))
    }
}
