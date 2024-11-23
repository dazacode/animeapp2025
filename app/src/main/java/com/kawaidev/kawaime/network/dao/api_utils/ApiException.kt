package com.kawaidev.kawaime.network.dao.api_utils

class ApiException(val status: Int?, message: String) : Exception(message)