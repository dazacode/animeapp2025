package com.kawaidev.kawaime.network.routes

object NewsRoutes {
    private const val BASE = "https://anime-api-eight-tawny.vercel.app/news/ann/"

    const val RECENT_NEWS = "${BASE}recent-feeds"
    const val NEW_INFO = "${BASE}INFO?id="
}