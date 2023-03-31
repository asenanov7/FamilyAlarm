package com.example.familyalarm.utils

sealed class UiState<out T> {

    object Default: UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<out T>(val result: T) : UiState<T>()
    data class Failure(val exceptionMessage:String): UiState<Nothing>()

}