package com.kawaidev.kawaime.network.dao.api_utils

object Errors {
    fun getErrorReasonByStatus(status: Int): String {
        return when (status) {
            400 -> "Bad Request: The server could not understand the request due to invalid syntax."
            401 -> "Unauthorized: The client must authenticate itself to get the requested response."
            403 -> "Forbidden: The client does not have access rights to the content."
            404 -> "Not Found: The server can not find the requested resource."
            500 -> "Internal Server Error: The server has encountered a situation it doesn't know how to handle."
            502 -> "Bad Gateway: The server, while acting as a gateway or proxy, received an invalid response from the upstream server."
            503 -> "Service Unavailable: The server is not ready to handle the request."
            504 -> "Gateway Timeout: The server, while acting as a gateway or proxy, did not get a timely response from the upstream server."
            else -> "Unknown Error: An unexpected error occurred."
        }
    }
}