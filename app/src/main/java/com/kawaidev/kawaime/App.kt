package com.kawaidev.kawaime

import android.app.Application
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class App : Application() {
    companion object {
        lateinit var instance: App
            private set

        lateinit var prefs: Prefs
            private set

        lateinit var httpClient: OkHttpClient
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        httpClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request()
                val response = chain.proceed(request)
                response
            }
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()

        prefs = Prefs.getInstance(this)
    }
}