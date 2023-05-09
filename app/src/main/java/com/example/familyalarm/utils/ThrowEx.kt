package com.example.familyalarm.utils

fun <T> throwEx(methodName:T):Nothing{
    throw RuntimeException(methodName.toString())
}