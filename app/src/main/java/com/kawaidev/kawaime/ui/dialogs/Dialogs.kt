package com.kawaidev.kawaime.ui.dialogs

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
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

    fun onError(message: String, description: String = "") {
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_layout_error, null)

        val errorMessage = dialogView.findViewById<TextView>(R.id.errorDesc)
        val negativeButton = dialogView.findViewById<Button>(R.id.ok_button)

        val descMessage = dialogView.findViewById<TextView>(R.id.detailsDesc)
        val descCard = dialogView.findViewById<CardView>(R.id.detailsCard)
        val descCardText = dialogView.findViewById<TextView>(R.id.descCardText)

        // If description is empty, hide the card and message
        if (description.isEmpty()) {
            descCard.visibility = View.GONE
            descMessage.visibility = View.GONE
        } else {
            descCard.visibility = View.VISIBLE

            // Otherwise, set the description text
            descMessage.text = description
            // Set the initial text for descCardText
            descCardText.text = "Click to show details"

            // Handle the click on descCard to toggle the visibility of descMessage
            descCard.setOnClickListener {
                if (descMessage.visibility == View.GONE) {
                    // Show the description and change the text of descCardText
                    descMessage.visibility = View.VISIBLE
                    descCardText.text = "Click to hide details"
                } else {
                    // Hide the description and change the text of descCardText
                    descMessage.visibility = View.GONE
                    descCardText.text = "Click to show details"
                }
            }
        }

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

    fun onQuality(onSelected: (String) -> Unit) {
        val dialog = QualityDialog.newInstance()
        dialog.setOnQualitySelectedListener { selectedQuality ->
            onSelected(selectedQuality)
        }
        dialog.show(activity.supportFragmentManager, "QualityDialog")
    }
}