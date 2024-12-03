package com.kawaidev.kawaime.network.routes

object StreamingRoutes {
    private const val BASE = "https://aniwatch-api-ochre.vercel.app/api/v2/"

    const val episodes = "${BASE}hianime/anime/"
    const val servers = "${BASE}hianime/episode/servers?animeEpisodeId="
    const val streaming = "${BASE}hianime/episode/sources"
}