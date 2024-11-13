package com.example.animeapp.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.kawaidev.kawaime.R

class SingleProgressSegmentView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val grayPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.progressBackground) // Set color correctly
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val segmentPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private var segmentPercentage: Float = 0f
    private var segmentColor: Int = ContextCompat.getColor(context, R.color.main)

    fun setSegment(percentage: Float) {
        this.segmentPercentage = percentage
        segmentPaint.color = segmentColor
        invalidate()  // Redraw the view with the new data
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val totalWidth = width.toFloat()
        val height = height.toFloat()
        val cornerRadius = 24f

        // Create a rounded rectangle path
        val path = Path().apply {
            addRoundRect(0f, 0f, totalWidth, height, cornerRadius, cornerRadius, Path.Direction.CW)
        }

        // Clip the canvas to the rounded rectangle path
        canvas.clipPath(path)

        // Draw the rounded rectangle background in gray
        canvas.drawRoundRect(0f, 0f, totalWidth, height, cornerRadius, cornerRadius, grayPaint)

        // Draw the colored segment with the same corner radius
        val segmentWidth = totalWidth * (segmentPercentage / 100) // Percentage should be between 0 and 100
        canvas.drawRoundRect(0f, 0f, segmentWidth, height, cornerRadius, cornerRadius, segmentPaint)
    }
}