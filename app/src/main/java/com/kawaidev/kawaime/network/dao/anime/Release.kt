package com.kawaidev.kawaime.network.dao.anime

import kotlinx.serialization.Serializable

@Serializable
data class Release(
    val anime: Anime? = null,
    val mostPopularAnimes: List<PopularAnime>? = null,
    val recommendedAnimes: List<BasicRelease>? = null,
    val relatedAnimes: List<BasicRelease>? = null,
    val seasons: List<Season>? = null,
    val screenshots: List<Screenshot>? = null
)

@Serializable
data class Anime(
    val info: AnimeInfo? = null,
    val moreInfo: MoreInfo? = null
)

@Serializable
data class AnimeInfo(
    val id: String? = null,
    val name: String? = null,
    val poster: String? = null,
    val description: String? = null,
    val stats: Stats? = null,
    val promotionalVideos: List<PromotionalVideo>? = null,
    val characterVoiceActor: List<CharacterVoiceActor>? = null
)

@Serializable
data class Stats(
    val rating: String? = null,
    val quality: String? = null,
    val episodes: EpisodeCounts? = null,
    val type: String? = null,
    val duration: String? = null
)

@Serializable
data class EpisodeCounts(
    val sub: Int? = null,
    val dub: Int? = null
)

@Serializable
data class PromotionalVideo(
    val title: String? = null,
    val source: String? = null,
    val thumbnail: String? = null
)

@Serializable
data class CharacterVoiceActor(
    val character: Character? = null,
    val voiceActor: VoiceActor? = null
)

@Serializable
data class Character(
    val id: String? = null,
    val poster: String? = null,
    val name: String? = null,
    val cast: String? = null
)

@Serializable
data class VoiceActor(
    val id: String? = null,
    val poster: String? = null,
    val name: String? = null,
    val cast: String? = null
)

@Serializable
data class MoreInfo(
    val japanese: String? = null,
    val synonyms: String? = null,
    val aired: String? = null,
    val premiered: String? = null,
    val duration: String? = null,
    val status: String? = null,
    val malscore: String? = null,
    val genres: List<String>? = null,
    val studios: String? = null,
    val producers: List<String>? = null
)

@Serializable
data class PopularAnime(
    val episodes: EpisodeCounts? = null,
    val id: String? = null,
    val jname: String? = null,
    val name: String? = null,
    val poster: String? = null,
    val type: String? = null
)

@Serializable
data class Season(
    val id: String? = null,
    val name: String? = null,
    val title: String? = null,
    val poster: String? = null,
    val isCurrent: Boolean? = null
)

@Serializable
data class Screenshot(
    val original: String,
    val preview: String
)