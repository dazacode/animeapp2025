package com.kawaidev.kawaime.ui.dialogs

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.card.MaterialCardView
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.ui.activity.MainActivity

class Dialogs(private val activity: MainActivity) {
    private var loadingDialog: LoadingDialog? = null

    fun onLoading(loading: Boolean) {
        val fragmentManager = activity.supportFragmentManager
        when (loading) {
            true -> {
                if (loadingDialog == null) {
                    loadingDialog = LoadingDialog.newInstance()
                    loadingDialog?.show(fragmentManager, "LoadingDialog")
                }
            }
            false -> {
                loadingDialog?.dismiss()
                loadingDialog = null
            }
        }
    }

    fun onHelp() {
        val fragmentManager = activity.supportFragmentManager
        HelpDialog.newInstance().show(fragmentManager, "HelpDialog")
    }

    fun onError(message: String) {
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_layout_error, null)

        val errorMessage = dialogView.findViewById<TextView>(R.id.errorDesc)
        val negativeButton = dialogView.findViewById<FrameLayout>(R.id.negative_button)

        val builder = AlertDialog.Builder(activity, R.style.CustomDialogStyle)
        builder.setView(dialogView)
        val dialog = builder.create()

        errorMessage.text = message

        negativeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

        val back = ColorDrawable(Color.TRANSPARENT)
        val inset = InsetDrawable(back, 48)

        dialog.window?.setBackgroundDrawable(inset)
    }
}
