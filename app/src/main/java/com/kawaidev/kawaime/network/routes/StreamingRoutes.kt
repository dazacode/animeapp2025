package com.kawaidev.kawaime.network.routes

object StreamingRoutes {
    private const val base = "https://aniwatch-api-ochre.vercel.app/api/v2/"

    const val episodes = "${base}hianime/anime/"
    const val servers = "${base}hianime/episode/servers?animeEpisodeId="
    const val streaming = "${base}hianime/episode/sources"
}