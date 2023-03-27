package com.example.familyalarm.domain.repositories

import com.example.familyalarm.utils.UiState

interface AuthRepository {

    suspend fun login(email:String, password:String): UiState<Boolean>

    suspend fun register(email:String, password:String): UiState<Boolean>

    suspend fun logOut(): UiState<Boolean>

    suspend fun resetPassword(email:String): UiState<Boolean>


}