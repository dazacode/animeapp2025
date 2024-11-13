package com.kawaidev.kawaime.network.dao.streaming

import kotlinx.serialization.Serializable

@Serializable
data class Streaming(
    val tracks: List<Track>? = null,
    val intro: Intro? = null,
    val outro: Outro? = null,
    val sources: List<Source>? = null,
    val anilistID: Int? = null,
    val malID: Int? = null
)

@Serializable
data class Track(
    val file: String? = null,
    val label: String? = null,
    val kind: String? = null,
    val default: Boolean = false
)

@Serializable
data class Intro(
    val start: Int? = null,
    val end: Int? = null
)

@Serializable
data class Outro(
    val start: Int? = null,
    val end: Int? = null
)

@Serializable
data class Source(
    val url: String? = null,
    val type: String? = null
)