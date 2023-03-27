package com.example.familyalarm.domain.repositories

interface AuthRepository {

    suspend fun login(email:String, password:String): Boolean

    suspend fun register(email:String, password:String): Boolean

    suspend fun logOut(): Boolean

    suspend fun resetPassword(email:String): Boolean


}