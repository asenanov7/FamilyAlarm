package com.example.familyalarm.utils

sealed class Resources<out T> {
    object Normal : Resources<Nothing>()
    data class Success<out T>(val result: T) :  Resources<T>()
    data class Fail(val exception: java.lang.Exception, ) :  Resources<Nothing>()
    object Loading :  Resources<Nothing>()

}