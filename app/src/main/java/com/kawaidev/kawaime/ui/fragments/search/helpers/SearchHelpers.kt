package com.kawaidev.kawaime.ui.fragments.search.helpers

import android.content.Context
import android.content.Intent
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.ui.activity.MainActivity
import com.kawaidev.kawaime.ui.adapters.helpers.GridRecycler
import com.kawaidev.kawaime.ui.fragments.filter.FilterFragment
import com.kawaidev.kawaime.ui.fragments.search.SearchFragment
import java.util.Locale

object SearchHelpers {

    fun setupAppBar(view: View, searchFragment: SearchFragment) {
        val tuneButton: ImageButton = view.findViewById(R.id.tune_button)
        val textField: TextInputEditText = view.findViewById(R.id.textField)

        textField.apply {
            imeOptions = EditorInfo.IME_ACTION_SEARCH
            inputType = InputType.TYPE_CLASS_TEXT

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
        }

        textField.post {
            textField.addTextChangedListener(textWatcher(searchFragment))

            updateVisibility(textField.text.isNullOrEmpty(), searchFragment)
            searchFragment.handleInitSearch()
        }

        tuneButton.setOnClickListener {
            val filterFragment = FilterFragment()
            (searchFragment.requireActivity() as MainActivity).pushFragment(filterFragment)
        }
    }

    private fun textWatcher(searchFragment: SearchFragment) = object : TextWatcher {
        private val handler = Handler(Looper.getMainLooper())
        private var searchRunnable: Runnable? = null

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val isEmpty = s.isNullOrEmpty()

            updateVisibility(isEmpty, searchFragment)

            searchRunnable?.let { handler.removeCallbacks(it) }
            searchRunnable = Runnable {
                if (!isEmpty) {
                    searchFragment.searchViewModel.searchAnime(s.toString())
                }
            }
            handler.postDelayed(searchRunnable!!, 300)
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    fun updateVisibility(isEmpty: Boolean, searchFragment: SearchFragment) {
        updateVisibilityBut(isEmpty, searchFragment)
        updateVisibilityRec(isEmpty, searchFragment)
    }

    fun updateVisibilityBut(isEmpty: Boolean, searchFragment: SearchFragment) {
        with(searchFragment) {
            clearButton.visibility = if (isEmpty) View.GONE else View.VISIBLE
            micButton.visibility = if (isEmpty) View.VISIBLE else View.GONE
        }
    }

    fun updateVisibilityRec(isEmpty: Boolean, searchFragment: SearchFragment) {
        with(searchFragment) {
            searchRecycler.visibility = if (isEmpty) View.GONE else View.VISIBLE
            historyRecycler.visibility = if (isEmpty) View.VISIBLE else View.GONE
        }
    }

    fun startVoiceSearch(result: ActivityResultLauncher<Intent>) {
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
                GridRecycler.setup(context, searchFragment.animeAdapter, searchFragment.searchRecycler, searchFragment.searchViewModel.searchResults.value ?: emptyList())
            }

            adapter = searchFragment.animeAdapter
            addOnScrollListener(scrollListener(searchFragment))

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

    private fun scrollListener(searchFragment: SearchFragment) = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            val layoutManager = recyclerView.layoutManager ?: return
            val totalItemCount = layoutManager.itemCount
            val lastVisibleItem = when (layoutManager) {
                is LinearLayoutManager -> layoutManager.findLastVisibleItemPosition()
                is StaggeredGridLayoutManager -> layoutManager.findLastVisibleItemPositions(null).maxOrNull() ?: 0
                else -> 0
            }
            if (!searchFragment.isLoading && searchFragment.hasNextPage && searchFragment.error == null) {
                if (totalItemCount <= lastVisibleItem + 5) {
                    searchFragment.searchViewModel.loadNextSearch()
                }
            }
        }
    }
}