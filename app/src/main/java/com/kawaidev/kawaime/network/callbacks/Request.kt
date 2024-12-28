package com.kawaidev.kawaime.network.callbacks

interface Request <T> {
    fun onStarted() {}
    fun onSuccess(data: T) {}
    fun onError(error: Exception) {}
}