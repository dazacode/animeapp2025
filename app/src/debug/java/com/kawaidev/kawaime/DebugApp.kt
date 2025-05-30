package com.kawaidev.kawaime

import android.os.StrictMode

class DebugApp : App() {
    override fun onCreate() {
        super.onCreate()
        
        // Enable StrictMode for development to catch performance issues early
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build()
        )
        
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build()
        )
    }
} 