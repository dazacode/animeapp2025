package com.kawaidev.kawaime.network.routes

object StreamingRoutes {
    private const val BASE = AnimeRoutes.BASE
    private const val ALT_BASE = "https://aniwatch-api-c8qh.onrender.com/api/v2/"

    const val EPISODES = "${BASE}hianime/anime/"
    const val SERVERS = "${BASE}hianime/episode/servers?animeEpisodeId="
    const val STREAMING = "${BASE}hianime/episode/sources"
}