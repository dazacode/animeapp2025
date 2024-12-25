package com.kawaidev.kawaime.ui.adapters.filter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckedTextView
import android.widget.ListView
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.ui.dialogs.GenresDialog

class GenreAdapter(
    private val dialog: GenresDialog,
    context: Context,
    private val genres: Array<String>,
    private var selectedGenres: BooleanArray
) : ArrayAdapter<String>(context, R.layout.multi_list_item, genres) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.multi_list_item, parent, false)
        val checkBox = view.findViewById<CheckedTextView>(R.id.text1)

        checkBox.text = genres[position]

        val listView = parent as ListView

        // Ensure the checked state is set correctly
        checkBox.isChecked = selectedGenres[position]

        checkBox.setOnClickListener {
            val isChecked = !selectedGenres[position]
            selectedGenres[position] = isChecked
            listView.setItemChecked(position, isChecked)

            dialog.onGenreCheckedChanged(selectedGenres)
        }

        return view
    }

    fun setGenres(genres: BooleanArray) {
        selectedGenres = genres
        notifyDataSetChanged()
    }

    fun getGenres(): BooleanArray {
        return selectedGenres
    }
}