package com.kawaidev.kawaime.ui.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.Strings
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
        val upperDescCardText = dialogView.findViewById<TextView>(R.id.upperDescCardText)

        if (description.isEmpty()) {
            descCard.visibility = View.GONE
            descMessage.visibility = View.GONE
        } else {
            descCard.visibility = View.VISIBLE

            descMessage.text = description
            descCardText.text = "Click to show details"

            descCard.setOnClickListener {
                if (descMessage.visibility == View.GONE) {
                    descMessage.visibility = View.VISIBLE
                    upperDescCardText.visibility = View.GONE
                    descCardText.text = "Click to hide details"
                } else {
                    descMessage.visibility = View.GONE
                    upperDescCardText.visibility = View.VISIBLE
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

    fun onGenres(view: MaterialAutoCompleteTextView) {
        val fragmentManager = activity.supportFragmentManager
        val genresDialog = GenresDialog.newInstance()

        val selectedGenresText = view.text.toString()
        val selectedGenresArray = selectedGenresText.split(", ").map { it.trim() }
        val selectedGenresBooleanArray = BooleanArray(Strings.genres.size) { false }

        // Mark the selected genres as true in the array
        for (i in Strings.genres.indices) {
            if (selectedGenresArray.contains(Strings.genres[i])) {
                selectedGenresBooleanArray[i] = true
            }
        }

        // Pass the selected genres to the dialog
        genresDialog.setInitialSelectedGenres(selectedGenresBooleanArray)

        genresDialog.setOnResetListener {
            view.setText("All")
        }

        genresDialog.setOnApplyListener { selectedGenres ->
            val selectedGenresString = selectedGenres.joinToString(", ")
            view.text = Editable.Factory.getInstance().newEditable(selectedGenresString)
        }

        genresDialog.show(fragmentManager, "GenresDialog")
    }

    fun onYear(view: MaterialAutoCompleteTextView) {
        val fragmentManager = activity.supportFragmentManager

        val yearDialog = YearDialog.newInstance()

        yearDialog.setOnResetListener {
            view.setText("All")
        }

        yearDialog.setOnApplyListener {  startDate, endDate ->
            val yearList = listOf(startDate, endDate)

            view.setText(yearList.joinToString(" - "))
        }

        if (view.text.toString() != "All") {
            val yearRange = view.text.toString().split(" - ")
            yearDialog.setInitialYears(yearRange[0].toInt(), yearRange[1].toInt())
        }

        yearDialog.show(fragmentManager, "GenresDialog")
    }
}