package com.kawaidev.kawaime.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.google.android.material.textfield.MaterialAutoCompleteTextView

class CustomAutoCompleteText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialAutoCompleteTextView(context, attrs, defStyleAttr) {

    private var onDisableClickListener: OnClickListener? = null

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!isEnabled && event?.action == MotionEvent.ACTION_DOWN) {
            onDisableClickListener?.onClick(this)
        }
        return super.onTouchEvent(event)
    }

    fun setOnDisableClickListener(l: OnClickListener?) {
        onDisableClickListener = l
    }
}