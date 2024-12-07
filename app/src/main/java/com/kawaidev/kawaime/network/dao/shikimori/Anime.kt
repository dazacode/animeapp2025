package com.kawaidev.kawaime.network.dao.shikimori

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Anime(
    val id: String? = null,
    val malId: String? = null,
    val name: String? = null,
    val russian: String? = null,
    val licenseNameRu: String? = null,
    val english: String? = null,
    val japanese: String? = null,
    val synonyms: List<String>? = null,
    val kind: String? = null,
    val rating: String? = null,
    val score: Double? = null,
    val status: String? = null,
    val episodes: Int? = null,
    val episodesAired: Int? = null,
    val duration: Int? = null,
    val airedOn: DateData? = null,
    val releasedOn: DateData? = null,
    val url: String? = null,
    val season: String? = null,
    val poster: Poster? = null,
    val fansubbers: List<String>? = null,
    val fandubbers: List<String>? = null,
    val licensors: List<String>? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val nextEpisodeAt: String? = null,
    val isCensored: Boolean? = null,
    val genres: List<Genre>? = null,
    val studios: List<Studio>? = null,
    val externalLinks: List<ExternalLink>? = null,
    val personRoles: List<PersonRole>? = null,
    val characterRoles: List<CharacterRole>? = null,
    val related: List<Related>? = null,
    val videos: List<Video>? = null,
    val screenshots: List<Screenshot>? = null
)

@Serializable
data class DateData(
    val year: Int? = null,
    val month: Int? = null,
    val day: Int? = null,
    val date: String? = null
)

@Serializable
data class Poster(
    val id: String? = null,
    @SerialName("originalUrl") val _originalUrl: String? = null,
    val mainUrl: String? = null
) {
    val originalUrl: String
        get() = _originalUrl?.replace("moe.shikimori.one", "desu.shikimori.one").toString()
}

@Serializable
data class Genre(
    val id: String? = null,
    val name: String? = null,
    val russian: String? = null,
    val kind: String? = null
)

@Serializable
data class Studio(
    val id: String? = null,
    val name: String? = null,
    val imageUrl: String? = null
)

@Serializable
data class ExternalLink(
    val id: String? = null,
    val kind: String? = null,
    val url: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

@Serializable
data class PersonRole(
    val id: String? = null,
    val rolesRu: List<String>? = null,
    val rolesEn: List<String>? = null,
    val person: Person? = null
)

@Serializable
data class Person(
    val id: String? = null,
    val name: String? = null,
    val poster: PosterReference? = null
)

@Serializable
data class PosterReference(
    val id: String? = null
)

@Serializable
data class CharacterRole(
    val id: String? = null,
    val rolesRu: List<String>? = null,
    val rolesEn: List<String>? = null,
    val character: Character? = null
)

@Serializable
data class Character(
    val id: String? = null,
    val name: String? = null,
    val poster: PosterReference? = null
)

@Serializable
data class Related(
    val id: String? = null,
    val anime: RelatedAnime? = null,
    val manga: RelatedAnime? = null,
    val relationKind: String? = null,
    val relationText: String? = null
)

@Serializable
data class RelatedAnime(
    val id: String? = null,
    val name: String? = null
)

@Serializable
data class Video(
    val id: String? = null,
    val url: String? = null
)

@Serializable
data class Screenshot(
    val id: String? = null,
    @SerialName("originalUrl") val _originalUrl: String? = null,
    val x166Url: String? = null,
    val x332Url: String? = null
) {
    val originalUrl: String
        get() = _originalUrl?.replace("moe.shikimori.one", "desu.shikimori.one").toString()
}