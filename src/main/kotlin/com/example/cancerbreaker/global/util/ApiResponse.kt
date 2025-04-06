package com.example.cancerbreaker.global.util

sealed class ApiResponse<out T> {
    data class Success<out T>(val data: T, val message: String) : ApiResponse<T>()
    data class Error(val message: String) : ApiResponse<Nothing>()

    fun <T> handleResponse(response: ApiResponse<T>) {
        when (response) {
            is Success -> println("Success: ${response.message}")
            is Error -> println("Error: ${response.message}")
        }
    }
}
