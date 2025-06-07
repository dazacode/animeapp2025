package com.kawaidev.kawaime.ui.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageButton
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
import com.kawaidev.kawaime.Prefs
import com.kawaidev.kawaime.ui.bottomSheets.BottomSheets
import com.kawaidev.kawaime.ui.dialogs.Dialogs
import com.kawaidev.kawaime.ui.fragments.details.DetailsFragment
import com.kawaidev.kawaime.ui.fragments.home.HomeFragment
import com.kawaidev.kawaime.ui.fragments.user.UserHomeFragment
import com.kawaidev.kawaime.ui.fragments.manga.MangaFragment
import com.kawaidev.kawaime.ui.fragments.search.SearchFragment
import com.ncapdevi.fragnav.FragNavController
import java.util.Locale

class MainActivity : AppCompatActivity(), FragNavController.TransactionListener, FragNavController.RootFragmentListener {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var fragNavController: FragNavController
    private lateinit var frame: LinearLayout
    private lateinit var bottomFrame: LinearLayout
    private lateinit var searchButton: ImageButton
    private lateinit var profileButton: ImageButton
    private lateinit var modalContainer: FrameLayout

    override val numberOfRootFragments: Int = 3

    val dialogs: Dialogs by lazy { Dialogs(this) }
    val bottomSheets: BottomSheets by lazy { BottomSheets(this) }

    private var backPressedOnce = false
    private var isModalOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check if user is logged in, if not redirect to WelcomeActivity
        val prefs = Prefs.getInstance(this)
        if (!prefs.isLoggedIn()) {
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
            return
        }
        
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
        searchButton = findViewById(R.id.btn_search)
        profileButton = findViewById(R.id.btn_profile)
        modalContainer = findViewById(R.id.modal_container)

        setupTopBarButtons()

        bottomNavigationView.setOnNavigationItemSelectedListener {
            bottomNavigationView.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(50)
                .withEndAction {
                    bottomNavigationView.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(100)
                        .start()
                }
                .start()
            
            when (it.itemId) {
                R.id.navigation_anime -> {
                    fragNavController.switchTab(FragNavController.TAB1)
                    true
                }
                R.id.navigation_home -> {
                    fragNavController.switchTab(FragNavController.TAB2)
                    true
                }
                R.id.navigation_manga -> {
                    fragNavController.switchTab(FragNavController.TAB3)
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

        handleDeepLink(intent)
    }

    private fun setupTopBarButtons() {
        searchButton.setOnClickListener {
            // Add search button animation
            searchButton.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(100)
                .withEndAction {
                    searchButton.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(100)
                        .start()
                }
                .start()
            
            // Navigate to search fragment as modal
            showSearchModal()
        }

        profileButton.setOnClickListener {
            // Add profile button animation
            profileButton.animate()
                .scaleX(0.9f)
                .scaleY(0.9f)
                .setDuration(100)
                .withEndAction {
                    profileButton.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(100)
                        .start()
                }
                .start()
            
            // Navigate to profile/user settings
            navigateToProfile()
        }
    }

    private fun showSearchModal() {
        if (isModalOpen) return
        
        val searchFragment = SearchFragment().apply {
            retainInstance = true
        }
        
        supportFragmentManager.beginTransaction()
            .replace(R.id.modal_container, searchFragment)
            .commitAllowingStateLoss()
        
        modalContainer.visibility = View.VISIBLE
        isModalOpen = true
    }

    fun hideSearchModal() {
        if (!isModalOpen) return
        
        val modalFragment = supportFragmentManager.findFragmentById(R.id.modal_container)
        if (modalFragment != null) {
            supportFragmentManager.beginTransaction()
                .remove(modalFragment)
                .commitAllowingStateLoss()
        }
        
        modalContainer.visibility = View.GONE
        isModalOpen = false
    }

    private fun navigateToSearch() {
        val searchFragment = SearchFragment().apply {
            retainInstance = true
        }
        pushFragment(searchFragment)
    }

    private fun navigateToProfile() {
        // For now, show a toast. You can implement profile fragment later
        showSnackbar("Profile feature coming soon!")
        
        // Uncomment when you have a profile fragment:
        // val profileFragment = ProfileFragment().apply {
        //     retainInstance = true
        // }
        // pushFragment(profileFragment)
    }

    private fun handleDeepLink(intent: Intent?) {
        val data: Uri? = intent?.data
        if (data != null) {
            val id = data.lastPathSegment
            if (id != null) {
                navigateToDetailsFragment(id)
            }
        }
    }

    private fun navigateToDetailsFragment(id: String) {
        val bundle = Bundle().apply {
            putString("id", id)
        }

        val detailsFragment = DetailsFragment().apply {
            arguments = bundle
        }

        pushFragment(detailsFragment)
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
        if (isModalOpen) {
            hideSearchModal()
            return
        }
        
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
            FragNavController.TAB2 -> UserHomeFragment().apply { retainInstance = true }
            FragNavController.TAB3 -> MangaFragment().apply { retainInstance = true }
            else -> throw IllegalStateException("Invalid tab index")
        }
    }

    fun showSnackbar(message: String) {
        val snackbar = Snackbar.make(findViewById(R.id.frame), message, Snackbar.LENGTH_LONG)

        snackbar.setAnchorView(bottomFrame)

        snackbar.setBackgroundTint(ContextCompat.getColor(this, R.color.snackBarBackground))

        val textView = snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        textView.includeFontPadding = false
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