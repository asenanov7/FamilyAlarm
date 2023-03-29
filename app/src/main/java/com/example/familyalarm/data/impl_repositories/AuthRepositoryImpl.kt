package com.example.familyalarm.data.impl_repositories

import android.util.Log
import com.example.familyalarm.domain.repositories.AuthRepository
import com.example.familyalarm.utils.UiState
import com.example.familyalarm.utils.Validation
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl : AuthRepository {

    private val auth by lazy { FirebaseAuth.getInstance() }
    override suspend fun login(email: String, password: String): UiState<Boolean> {
        val result:UiState<Boolean> =
            try {
                if (Validation.isEmailValid(email) && password.length > 7) {
                    auth.signInWithEmailAndPassword(email, password).await()
                    UiState.Success(true)
                }else{
                    UiState.Failure("Too short password or wrong email")
                }
            } catch (e: Exception) {
                UiState.Failure("${e.message}")
            }
        return result
    }

    override suspend fun register(email: String, password: String): UiState<Boolean>{
        val result: UiState<Boolean> =
            try {
                if (Validation.isEmailValid(email) && password.length > 7) {
                    auth.createUserWithEmailAndPassword(email, password).await()
                    UiState.Success(true)
                } else {
                    UiState.Failure("Too short password or wrong email")
                }
            }catch (e: Exception){
                UiState.Failure("${e.message}")
            }
        return result
    }

    override suspend fun logOut(): UiState<Boolean> {
        val result: UiState<Boolean> =
            try {
                if (auth.currentUser!=null) {
                    auth.signOut()
                    UiState.Success(true)
                }else{
                    throw Exception("currentUser = null")
                }
            }catch (e:Exception){
                UiState.Failure("${e.message}")
            }
        return result
    }

    override suspend fun resetPassword(email: String): UiState<Boolean> {
        val result: UiState<Boolean> =
            try {
                if (Validation.isEmailValid(email)){
                    auth.sendPasswordResetEmail(email).await()
                    UiState.Success(true)
                }else{
                    UiState.Failure("Wrong email")
                }
            }catch (e: Exception){
                UiState.Failure("${e.message}")
            }
        return result
    }

}