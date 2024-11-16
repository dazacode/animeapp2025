package com.kawaidev.kawaime.utils

import android.content.Context

object Converts {
    fun dpToPx(dp: Float, context: Context): Float {
        return dp * context.resources.displayMetrics.density
    }
}