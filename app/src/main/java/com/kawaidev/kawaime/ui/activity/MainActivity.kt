package com.kawaidev.kawaime.ui.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.ui.bottomSheets.BottomSheets
import com.kawaidev.kawaime.ui.dialogs.Dialogs
import com.kawaidev.kawaime.ui.fragments.favorite.FavoriteFragment
import com.kawaidev.kawaime.ui.fragments.explore.ExploreFragment
import com.kawaidev.kawaime.ui.fragments.home.HomeFragment
import com.kawaidev.kawaime.ui.fragments.search.SearchExploreFragment
import com.ncapdevi.fragnav.FragNavController
import java.util.Locale

class MainActivity : AppCompatActivity(), FragNavController.TransactionListener, FragNavController.RootFragmentListener {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var fragNavController: FragNavController
    private lateinit var frame: LinearLayout
    private lateinit var bottomFrame: LinearLayout

    override val numberOfRootFragments: Int = 4

    val dialogs: Dialogs by lazy { Dialogs(this) }
    val bottomSheets: BottomSheets by lazy { BottomSheets(this) }

    private var backPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        fragNavController = FragNavController(supportFragmentManager, R.id.frame).apply {
            transactionListener = this@MainActivity
            rootFragmentListener = this@MainActivity
            fragmentHideStrategy = FragNavController.HIDE
            initialize(FragNavController.TAB1, savedInstanceState)
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        frame = findViewById(R.id.frameLinear)
        bottomFrame = findViewById(R.id.bottomNavigationContainer)

        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    fragNavController.switchTab(FragNavController.TAB1)
                    true
                }
                R.id.navigation_explore -> {
                    fragNavController.switchTab(FragNavController.TAB2)
                    true
                }
                R.id.navigation_search -> {
                    fragNavController.switchTab(FragNavController.TAB3)
                    true
                }
                R.id.navigation_favorite -> {
                    fragNavController.switchTab(FragNavController.TAB4)
                    true
                }
                else -> false
            }
        }

        bottomNavigationView.setOnNavigationItemReselectedListener {
            fragNavController.clearStack();
        }

        bottomFrame.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val layoutParams = frame.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.bottomMargin = bottomFrame.height
                frame.layoutParams = layoutParams

                bottomFrame.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    fun pushFragment(fragment: Fragment) {
        fragment.retainInstance = true

        fragNavController.pushFragment(fragment)
    }

    fun popFragment() {
        fragNavController.popFragment()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("tab_index", fragNavController.currentStackIndex)
        fragNavController.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        if (fragNavController.isRootFragment) {
            if (backPressedOnce) {
                super.onBackPressed()
            } else {
                backPressedOnce = true
            }
        } else if (fragNavController.popFragment()) {
            backPressedOnce = false
        }
    }

    override fun getRootFragment(index: Int): Fragment {
        return when (index) {
            FragNavController.TAB1 -> HomeFragment().apply { retainInstance = true }
            FragNavController.TAB2 -> ExploreFragment().apply { retainInstance = true }
            FragNavController.TAB3 -> SearchExploreFragment().apply { retainInstance = true }
            FragNavController.TAB4 -> FavoriteFragment().apply { retainInstance = true }
            else -> throw IllegalStateException("Invalid tab index")
        }
    }

    fun showSnackbar(message: String) {
        val snackbar = Snackbar.make(findViewById(R.id.frame), message, Snackbar.LENGTH_LONG)

        snackbar.setAnchorView(bottomFrame)

        snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.snackBarBackground))

        val textView = snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.setTextColor(ContextCompat.getColor(this, R.color.snackBarText))

        snackbar.show()
    }

    fun copyToClipboard(label: String, text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show()
    }

    override fun onFragmentTransaction(fragment: Fragment?, transactionType: FragNavController.TransactionType) {}

    override fun onTabTransaction(fragment: Fragment?, index: Int) {}
}