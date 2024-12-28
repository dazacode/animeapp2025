package com.kawaidev.kawaime.network.routes

object AnimeRoutes {
    const val BASE = "https://aniwatch-api-git.vercel.app/api/v2/"
    const val BASE_JAVA = "http://api.veanime.cc:8080/"
    private const val BASE_JAVA_API = "${BASE_JAVA}api/v1/"

    const val INFO = "${BASE_JAVA_API}anime/"
    const val SEARCH = "${BASE}hianime/search"
    const val SPOTLIGHT = "${BASE}hianime/home"
    const val CATEGORY = "${BASE}hianime/category/"
    const val SCHEDULE = "${BASE}hianime/schedule?date="
}