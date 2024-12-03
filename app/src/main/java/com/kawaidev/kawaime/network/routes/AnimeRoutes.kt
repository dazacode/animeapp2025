package com.kawaidev.kawaime.network.routes

object AnimeRoutes {
    private const val BASE = "https://aniwatch-api-ochre.vercel.app/api/v2/"
    private const val BASE_JAVA = "http://164.90.166.92:8080/api/v1/"
    const val SHIKIMORI = "https://shikimori.one"

    const val info = "${BASE_JAVA}anime/"
    const val search = "${BASE}hianime/search"
    const val spotlight = "${BASE}hianime/home"
    const val category = "${BASE}hianime/category/"
    const val schedule = "${BASE}hianime/schedule?date="
}