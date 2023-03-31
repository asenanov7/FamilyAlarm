package com.example.familyalarm.domain.repositories

import com.example.familyalarm.utils.UiState

interface AuthRepository {

    suspend fun login(email:String, password:String)

    suspend fun register(email:String, password:String)

    suspend fun logOut()

    suspend fun resetPassword(email:String)


}