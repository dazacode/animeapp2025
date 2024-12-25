package com.kawaidev.kawaime.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout
import java.lang.reflect.Field

class CustomTextInputLayout(context: Context?, attrs: AttributeSet?) :
    TextInputLayout(context!!, attrs) {
    override fun setHelperTextEnabled(enabled: Boolean) {
        super.setHelperTextEnabled(enabled)

        if (!enabled) {
            return
        }

        try {
            val helperViewField: Field = TextInputLayout::class.java.getDeclaredField("helperTextView")
            helperViewField.isAccessible = true
            val helperView = helperViewField.get(this) as TextView
            if (helperView != null) {
                helperView.gravity = Gravity.START
                helperView.setPadding(0, helperView.paddingTop, helperView.paddingRight, helperView.paddingBottom)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}