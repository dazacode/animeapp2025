package com.kawaidev.kawaime.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.Strings
import com.kawaidev.kawaime.ui.activity.MainActivity
import com.kawaidev.kawaime.ui.adapters.filter.GenreAdapter

class GenresDialog : DialogFragment() {

    private lateinit var genresArray: Array<String>
    private lateinit var selectedGenres: BooleanArray

    private lateinit var genresList: ListView
    private lateinit var selectAllButton: TextView

    private var onResetListener: (() -> Unit)? = null
    private var onApplyListener: ((List<String>) -> Unit)? = null

    // Setter functions for the listeners
    fun setOnResetListener(listener: () -> Unit) {
        onResetListener = listener
    }

    fun setOnApplyListener(listener: (List<String>) -> Unit) {
        onApplyListener = listener
    }

    fun setInitialSelectedGenres(selectedGenresBooleanArray: BooleanArray) {
        selectedGenres = selectedGenresBooleanArray.copyOf()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = requireContext()

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_layout_genres, null)

        val negativeButton = dialogView.findViewById<Button>(R.id.reset_button)
        val positiveButton = dialogView.findViewById<Button>(R.id.apply_button)
        selectAllButton = dialogView.findViewById(R.id.dialog_title_btn)

        genresArray = Strings.genres

        if (!::selectedGenres.isInitialized) {
            selectedGenres = BooleanArray(genresArray.size) { false }
        }

        genresList = dialogView.findViewById(R.id.items_list)
        val adapter = GenreAdapter(this, context, genresArray, selectedGenres)
        genresList.adapter = adapter

        genresList.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        for (i in selectedGenres.indices) {
            genresList.setItemChecked(i, selectedGenres[i])
        }

        updateSelectAllButton(selectAllButton, selectedGenres)

        val builder = AlertDialog.Builder(context, R.style.CustomDialogStyle)
        builder.setView(dialogView)

        val dialog = builder.create()

        positiveButton.setOnClickListener {
            val selected = mutableListOf<String>()

            selectedGenres = adapter.getGenres()

            for (i in selectedGenres.indices) {
                if (selectedGenres[i]) {
                    selected.add(genresArray[i])
                }
            }

            if (selected.isEmpty()) {
                (requireActivity() as MainActivity).dialogs.onError("No genres selected")
                return@setOnClickListener
            }

            onApplyListener?.invoke(selected)
            dialog.dismiss()
        }

        negativeButton.setOnClickListener {
            onResetListener?.invoke()
            dialog.dismiss()
        }

        selectAllButton.setOnClickListener {
            val allSelected = selectedGenres.all { it }
            if (allSelected) {
                selectedGenres.fill(false)
                for (i in selectedGenres.indices) {
                    genresList.setItemChecked(i, false)
                }
            } else {
                selectedGenres.fill(true)
                for (i in selectedGenres.indices) {
                    genresList.setItemChecked(i, true)
                }
            }
            updateSelectAllButton(selectAllButton, selectedGenres)
        }

        dialog.window?.setBackgroundDrawable(InsetDrawable(ColorDrawable(Color.TRANSPARENT), 48))

        return dialog
    }

    private fun updateSelectAllButton(button: TextView, selectedGenres: BooleanArray) {
        val allSelected = selectedGenres.all { it }
        button.text = if (allSelected) "Remove all" else "Select all"
    }

    fun onGenreCheckedChanged(selectedGenres: BooleanArray) {
        this@GenresDialog.selectedGenres = selectedGenres
        updateSelectAllButton(selectAllButton, selectedGenres)
    }

    companion object {
        fun newInstance(): GenresDialog {
            return GenresDialog()
        }
    }
}