package com.kawaidev.kawaime.network.routes

object AnimeRoutes {
    private const val base = "https://aniwatch-api-ochre.vercel.app/api/v2/"

    const val info = "${base}hianime/anime/"
    const val search = "${base}hianime/search"
    const val spotlight = "${base}hianime/home"
    const val category = "${base}hianime/category/"
}