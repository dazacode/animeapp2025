package com.example.animeapp.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.kawaidev.kawaime.R

class ProgressSegmentView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val statusesMap = mapOf(
        0 to "Not watching",
        1 to "Watching",
        2 to "In plans",
        3 to "Watched",
        4 to "On hold",
        5 to "Dropped"
    )

    fun getStatusColor(status: Int, context: Context): Int {
//        return when (status) {
//            1 -> ContextCompat.getColor(context, R.color.statusColor5)
//            2 -> ContextCompat.getColor(context, R.color.statusColor6)
//            3 -> ContextCompat.getColor(context, R.color.statusColor7)
//            4 -> ContextCompat.getColor(context, R.color.statusColor8)
//            5 -> ContextCompat.getColor(context, R.color.statusColor9)
//            else -> Color.TRANSPARENT
//        }
        return  Color.TRANSPARENT
    }

    private val watchingPaint = Paint().apply {
        color = getStatusColor(1, context)
        style = Paint.Style.FILL
        isAntiAlias = true // Enable anti-aliasing
    }
    private val inPlansPaint = Paint().apply {
        color = getStatusColor(2, context)
        style = Paint.Style.FILL
        isAntiAlias = true // Enable anti-aliasing
    }
    private val watchedPaint = Paint().apply {
        color = getStatusColor(3, context)
        style = Paint.Style.FILL
        isAntiAlias = true // Enable anti-aliasing
    }
    private val onHoldPaint = Paint().apply {
        color = getStatusColor(4, context)
        style = Paint.Style.FILL
        isAntiAlias = true // Enable anti-aliasing
    }
    private val droppedPaint = Paint().apply {
        color = getStatusColor(5, context)
        style = Paint.Style.FILL
        isAntiAlias = true // Enable anti-aliasing
    }

    private var statusDistribution: Map<Int, Float> = mapOf()

    fun setStatusDistribution(distribution: Map<Int, Float>) {
        this.statusDistribution = distribution
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (statusDistribution.isEmpty()) return

        val totalWidth = width.toFloat()
        val height = height.toFloat()
        val cornerRadius = 8f
        var currentPosition = 0f

        // Draw the rounded rectangle background
        val path = Path().apply {
            addRoundRect(0f, 0f, totalWidth, height, cornerRadius, cornerRadius, Path.Direction.CW)
        }
        canvas.clipPath(path)

        // Draw each segment based on the status and its distribution percentage
        statusDistribution.forEach { (status, percentage) ->
            val paint = when (status) {
                1 -> watchingPaint
                2 -> inPlansPaint
                3 -> watchedPaint
                4 -> onHoldPaint
                5 -> droppedPaint
                else -> null
            }

            paint?.let {
                val segmentWidth = totalWidth * percentage
                canvas.drawRect(currentPosition, 0f, currentPosition + segmentWidth, height, it)
                currentPosition += segmentWidth
            }
        }
    }
}