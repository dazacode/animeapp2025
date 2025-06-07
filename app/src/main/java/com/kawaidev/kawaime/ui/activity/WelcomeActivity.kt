package com.kawaidev.kawaime.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.LinearLayout
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.kawaidev.kawaime.R
import com.kawaidev.kawaime.Prefs
import com.kawaidev.kawaime.network.dao.anime.BasicRelease
import com.kawaidev.kawaime.network.interfaces.AnimeService
import com.kawaidev.kawaime.ui.activity.auth.AuthActivity
import com.kawaidev.kawaime.utils.LoadImage
import kotlinx.coroutines.launch

class WelcomeActivity : AppCompatActivity() {
    
    private lateinit var backgroundImage: ImageView
    private lateinit var logoImage: ImageView
    private lateinit var welcomeText: TextView
    private lateinit var animeInfoCard: CardView
    private lateinit var animeInfoContainer: LinearLayout
    private lateinit var animeTitle: TextView
    private lateinit var animeDescription: TextView
    private lateinit var animeRating: TextView
    private lateinit var animeType: TextView
    private lateinit var animeEpisodes: TextView
    private lateinit var dropdownIcon: ImageView
    private lateinit var loginButton: Button
    private lateinit var devOnlyText: TextView
    
    private val animeService = AnimeService.create()
    private var isDropdownExpanded = false
    private var currentAnime: BasicRelease? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        
        initViews()
        setupClickListeners()
        loadRandomAnime()
    }
    
    private fun initViews() {
        backgroundImage = findViewById(R.id.background_image)
        logoImage = findViewById(R.id.logo_image)
        welcomeText = findViewById(R.id.welcome_text)
        animeInfoCard = findViewById(R.id.anime_info_card)
        animeInfoContainer = findViewById(R.id.anime_info_container)
        animeTitle = findViewById(R.id.anime_title)
        animeDescription = findViewById(R.id.anime_description)
        animeRating = findViewById(R.id.anime_rating)
        animeType = findViewById(R.id.anime_type)
        animeEpisodes = findViewById(R.id.anime_episodes)
        dropdownIcon = findViewById(R.id.dropdown_icon)
        loginButton = findViewById(R.id.login_button)
        devOnlyText = findViewById(R.id.dev_only_text)
        
        // Set logo
        logoImage.setImageResource(R.drawable.zantaku_64x64)
        
        // Set dropdown icon tint
        dropdownIcon.setColorFilter(ContextCompat.getColor(this, R.color.white))
        
        // Initially hide detailed info
        animeInfoContainer.visibility = View.GONE
    }
    
    private fun setupClickListeners() {
        animeInfoCard.setOnClickListener {
            toggleDropdown()
        }
        
        loginButton.setOnClickListener {
            // Navigate to 1anime login
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
        }
        
        devOnlyText.setOnClickListener {
            // Dev only login - skip to main app for development
            val prefs = Prefs.getInstance(this@WelcomeActivity)
            prefs.setLoggedIn(true)
            val intent = Intent(this@WelcomeActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    
    private fun toggleDropdown() {
        isDropdownExpanded = !isDropdownExpanded
        
        if (isDropdownExpanded) {
            animeInfoContainer.visibility = View.VISIBLE
            dropdownIcon.rotation = 180f
        } else {
            animeInfoContainer.visibility = View.GONE
            dropdownIcon.rotation = 0f
        }
        
        // Animate the rotation
        dropdownIcon.animate()
            .rotation(if (isDropdownExpanded) 180f else 0f)
            .setDuration(200)
            .start()
    }
    
    private fun loadRandomAnime() {
        lifecycleScope.launch {
            try {
                val homeData = animeService.getHome()
                val allAnimes = mutableListOf<BasicRelease>().apply {
                    addAll(homeData.spotlightAnimes)
                    addAll(homeData.trendingAnimes)
                    addAll(homeData.mostPopularAnimes)
                    addAll(homeData.topAiringAnimes)
                }
                
                if (allAnimes.isNotEmpty()) {
                    currentAnime = allAnimes.random()
                    displayAnimeInfo(currentAnime!!)
                }
            } catch (e: Exception) {
                // Fallback to default welcome message
                animeTitle.text = "Welcome to Zantaku"
                animeDescription.text = "Your gateway to amazing anime content"
            }
        }
    }
    
    private fun displayAnimeInfo(anime: BasicRelease) {
        // Load background image
        anime.poster?.let { posterUrl ->
            LoadImage().loadImage(this, posterUrl, backgroundImage)
        }
        
        // Set anime info
        animeTitle.text = anime.name ?: "Unknown Anime"
        animeDescription.text = anime.description ?: "No description available"
        animeRating.text = "Rating: ${anime.rating ?: "N/A"}"
        animeType.text = "Type: ${anime.type ?: "N/A"}"
        
        val subEpisodes = anime.episodes?.sub ?: 0
        val dubEpisodes = anime.episodes?.dub ?: 0
        animeEpisodes.text = "Episodes: $subEpisodes SUB${if (dubEpisodes > 0) " / $dubEpisodes DUB" else ""}"
    }
} 