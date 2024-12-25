package com.kawaidev.kawaime.ui.adapters.filter.helpers

import com.kawaidev.kawaime.Strings

enum class FilterEnum(val label: String) {
    TYPE("Type"),
    STATUS("Status"),
    RATED("Rating"),
    SCORE("Score"),
    SEASON("Season"),
    LANGUAGE("Language"),
    DATE("Year"),
    SORT("Sort"),
    GENRES("Genres");

    fun getList(): List<String> {
        return when (this) {
            TYPE -> Strings.types.toList()
            STATUS -> Strings.statuses.toList()
            RATED -> Strings.rated.toList()
            SCORE -> Strings.scores.toList()
            SEASON -> Strings.seasons.toList()
            LANGUAGE -> Strings.languages.toList()
            DATE -> emptyList()
            SORT -> Strings.sorts.toList()
            GENRES -> Strings.genres.toList()
        }
    }

    companion object {
        fun getEnum(label: String): FilterEnum? {
            return values().find { it.label.equals(label, ignoreCase = true) }
        }
    }
}