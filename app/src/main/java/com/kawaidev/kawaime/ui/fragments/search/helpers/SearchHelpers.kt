package com.kawaidev.kawaime.ui.fragments.search.helpers

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageButton
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.textfield.TextInputEditText
import com.grzegorzojdana.spacingitemdecoration.Spacing
import com.grzegorzojdana.spacingitemdecoration.SpacingItemDecoration
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.ui.adapters.anime.AnimeAdapter
import com.kawaidev.kawaime.ui.adapters.anime.helpers.AnimeViewType
import com.kawaidev.kawaime.ui.adapters.helpers.GridRecycler
import com.kawaidev.kawaime.ui.fragments.search.SearchFragment
import com.kawaidev.kawaime.utils.Converts
import java.util.Locale

object SearchHelpers {

    fun setupAppBar(view: View, searchFragment: SearchFragment) {
        val tuneButton: ImageButton = view.findViewById(R.id.tune_button)
        val textField: TextInputEditText = view.findViewById(R.id.textField)

        textField.apply {
            imeOptions = EditorInfo.IME_ACTION_SEARCH
            inputType = InputType.TYPE_CLASS_TEXT
            addTextChangedListener(textWatcher(searchFragment))
            setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) showSoftKeyboard(v) else hideSoftKeyboard(v)
            }
            setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE || event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                    searchFragment.prefs.addSearchTerm(text.toString())
                    hideSoftKeyboard(v)
                    true
                } else false
            }

            if (searchFragment.initSearch.isNullOrEmpty().not()) {
                setText(searchFragment.initSearch)
                searchFragment.historyRecycler.visibility = View.GONE
                searchFragment.searchRecycler.visibility = View.VISIBLE
            } else requestFocus()
        }

        if (searchFragment.initSearch.isNullOrEmpty()) {
            searchFragment.micButton.visibility = View.VISIBLE
            searchFragment.clearButton.visibility = View.GONE
        } else {
            searchFragment.micButton.visibility = View.GONE
            searchFragment.clearButton.visibility = View.VISIBLE
        }

        searchFragment.micButton.setOnClickListener {
            hideSoftKeyboard(view)
            startVoiceSearch(searchFragment.result)
        }
        searchFragment.clearButton.setOnClickListener {
            searchFragment.clearText()
            searchFragment.historyRecycler.visibility = View.VISIBLE
            searchFragment.searchRecycler.visibility = View.GONE
        }
        tuneButton.setOnClickListener {}
    }

    private fun textWatcher(searchFragment: SearchFragment) = object : TextWatcher {
        private val handler = Handler(Looper.getMainLooper())
        private var searchRunnable: Runnable? = null

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val isEmpty = s.isNullOrEmpty()

            Log.d("Search", "Text changed")

            searchFragment.clearButton.visibility = if (isEmpty) View.GONE else View.VISIBLE
            searchFragment.micButton.visibility = if (isEmpty) View.VISIBLE else View.GONE

            searchRunnable?.let { handler.removeCallbacks(it) }

            searchRunnable = Runnable {
                if (!isEmpty) {
                    searchFragment.searchRecycler.visibility = View.VISIBLE
                    searchFragment.historyRecycler.visibility = View.GONE
                    searchFragment.searchViewModel.searchAnime(s.toString())
                } else {
                    searchFragment.searchViewModel.clearAnimeList()
                    searchFragment.searchRecycler.visibility = View.GONE
                    searchFragment.historyRecycler.visibility = View.VISIBLE
                }
            }

            handler.postDelayed(searchRunnable!!, 300)
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    private fun startVoiceSearch(result: ActivityResultLauncher<Intent>) {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "You can speak now")
            }
            result.launch(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setupRecyclerView(context: Context, searchFragment: SearchFragment) {
        searchFragment.historyRecycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = searchFragment.historyAdapter
        }

        searchFragment.searchRecycler.apply {
            post {
                GridRecycler.setup(context, searchFragment.animeAdapter, searchFragment.searchRecycler)
            }
            adapter = searchFragment.animeAdapter
            addOnScrollListener(searchFragment.scrollListener())

            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }

        searchFragment.historyAdapter.updateData(searchFragment.prefs.getSearches())
    }

    fun showSoftKeyboard(view: View) {
        (view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?)
            ?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    fun hideSoftKeyboard(view: View) {
        (view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?)
            ?.hideSoftInputFromWindow(view.windowToken, 0)
    }
}