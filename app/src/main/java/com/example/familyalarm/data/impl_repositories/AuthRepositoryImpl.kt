package com.example.familyalarm.data.impl_repositories

import android.util.Log
import com.example.familyalarm.domain.repositories.AuthRepository
import com.example.familyalarm.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl : AuthRepository {

    private val auth by lazy { FirebaseAuth.getInstance() }
    override suspend fun login(email: String, password: String): Boolean {
        val result:Boolean =
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                true
            } catch (e: Exception) {
                Log.d("ARSEN", "resetPassword: ${e.message}")
                false
            }
        return result
    }

    override suspend fun register(email: String, password: String): Boolean{
        val result: Boolean =
            try {
                if (Utils.isEmailValid(email)) {
                    auth.createUserWithEmailAndPassword(email, password).await()
                    true
                } else {
                    true
                }
            }catch (e: Exception){
                Log.d("ARSEN", "resetPassword: ${e.message}")
                false
            }
        return result
    }

    override suspend fun logOut(): Boolean {
        val result: Boolean =
            try {
                true
            }catch (e:Exception){
                Log.d("ARSEN", "resetPassword: ${e.message}")
                false
            }
        return result
    }

    override suspend fun resetPassword(email: String): Boolean {
        val result: Boolean =
            try {
                if (Utils.isEmailValid(email)){
                    auth.sendPasswordResetEmail(email).await()
                    true
                }else{
                    false
                }
            }catch (e: Exception){
                Log.d("ARSEN", "resetPassword: ${e.message}")
                false
            }
        return result
    }


/* override fun register(email: String, password: String): AuthStates {
    val stateFlow: MutableStateFlow<AuthStates> = MutableStateFlow(AuthStates.Loading)
    if (Utils.isEmailValid(email)) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { stateFlow.value = AuthStates.Success("Success") }
            .addOnFailureListener { stateFlow.value = AuthStates.Failure("${it.message}") }
    } else {
        stateFlow.value = AuthStates.Failure("Not valid")
    }
    return stateFlow
}

override fun logOut(): AuthStates {
    val stateFlow: MutableStateFlow<AuthStates> = MutableStateFlow(AuthStates.Loading)
    if (auth.currentUser!=null) {
        auth.signOut()
        while (auth.currentUser != null) {
            stateFlow.value = AuthStates.Loading
        }
        stateFlow.value = AuthStates.Success("SignOut: Success")
    }
    return stateFlow
}

override fun resetPassword(email: String): AuthStates {
    val stateFlow: MutableStateFlow<AuthStates> = MutableStateFlow(AuthStates.Loading)
        if (Utils.isEmailValid(email)) {
            auth.sendPasswordResetEmail(email)
                .addOnSuccessListener { stateFlow.value = AuthStates.Success("Reset: Success") }
                .addOnFailureListener { stateFlow.value = AuthStates.Failure("Reset: Failure $it") }
        } else {
            stateFlow.value = AuthStates.Failure("Not valid")
        }
        return stateFlow
}*/
}