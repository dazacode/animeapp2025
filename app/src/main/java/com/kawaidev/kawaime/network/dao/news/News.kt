package com.kawaidev.kawaime.network.dao.news

import kotlinx.serialization.Serializable

@Serializable
data class News(
    val id: String? = null,
    val title: String? = null,
    val uploadedAt: String? = null,
    val topics: List<String>? = null,
    val preview: Preview? = null,
    val intro: String? = null,
    val description: String? = null,
    val thumbnail: String? = null,
    val thumbnailHash: String? = null,
    val url: String? = null
)

@Serializable
data class Preview(
    val intro: String,
    val full: String
)