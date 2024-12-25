package com.kawaidev.kawaime.network.dao.api_utils

import kotlinx.serialization.Serializable

@Serializable
data class SearchParams(
    val q: String = "",           // The SEARCH query (title of the item)
    var page: Int = 1,            // The page number of the result, default is 1
    val type: String? = null,     // Type of the anime (e.g., movie)
    val status: String? = null,   // Status of the anime (e.g., finished-airing)
    val rated: String? = null,    // Rating of the anime (e.g., r+ or pg-13)
    val score: String? = null,    // Score of the anime (e.g., good or very-good)
    val season: String? = null,   // Season of the anime (e.g., spring)
    val language: String? = null, // Language CATEGORY of the anime (e.g., sub or sub-&-dub)
    val start_date: String? = null, // Start date (yyyy-mm-dd)
    val end_date: String? = null,   // End date (yyyy-mm-dd)
    val sort: String? = null,       // Order of sorting (e.g., recently-added)
    val genres: String? = null      // Genre(s) of the anime, separated by commas
)