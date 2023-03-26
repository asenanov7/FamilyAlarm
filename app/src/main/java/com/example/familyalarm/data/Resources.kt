package com.example.familyalarm.data

sealed class Resources<out T> {
    data class Success<out T>(val result: T) :  Resources<T>()
    data class Fail(val exception: java.lang.Exception, ) :  Resources<Nothing>()
    object Loading :  Resources<Nothing>()

}