package com.kawaidev.kawaime

import android.app.Application
import android.content.Intent
import android.content.res.Configuration
import com.kawaidev.kawaime.ui.activity.MainActivity
import okhttp3.OkHttpClient
import java.util.Locale
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
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        prefs = Prefs.getInstance(this)
    }
}