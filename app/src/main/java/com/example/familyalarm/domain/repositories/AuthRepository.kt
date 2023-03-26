package com.example.familyalarm.domain.repositories

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {

    fun login(email:String, password:String): StateFlow<AuthStates>

    fun register(email:String, password:String): StateFlow<AuthStates>

    fun logOut(): StateFlow<AuthStates>

    fun resetPassword(email:String): StateFlow<AuthStates>


    sealed class AuthStates(){
        object Loading: AuthStates()
        data class Success(val result: String): AuthStates()
        data class Failure(val exMessage: String): AuthStates()
    }

}