package com.example.familyalarm.domain.repositories

interface AuthRepository {

    suspend fun login(email:String, password:String): AuthStates

    suspend fun register(email:String, password:String): AuthStates

    suspend fun logOut(): AuthStates

    suspend fun resetPassword(email:String): AuthStates


    sealed class AuthStates(){
        data class Success(val result: String): AuthStates()
        data class Failure(val exMessage: String): AuthStates()
    }

}