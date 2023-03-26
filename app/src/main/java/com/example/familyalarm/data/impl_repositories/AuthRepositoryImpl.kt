package com.example.familyalarm.data.impl_repositories

import com.example.familyalarm.data.Utils
import com.example.familyalarm.domain.repositories.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl : AuthRepository {

    private val auth by lazy { FirebaseAuth.getInstance() }
    override suspend fun login(email: String, password: String): AuthRepository.AuthStates {
        val result: AuthRepository.AuthStates =
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                AuthRepository.AuthStates.Success("Login Success")
            } catch (e: Exception) {
                AuthRepository.AuthStates.Failure("Fail: ${e.message}")
            }
        return result
    }

    override suspend fun register(email: String, password: String): AuthRepository.AuthStates {
        val result: AuthRepository.AuthStates  =
            try {
                if (Utils.isEmailValid(email)) {
                    auth.createUserWithEmailAndPassword(email, password).await()
                    AuthRepository.AuthStates.Success("Register Success")
                } else {
                    AuthRepository.AuthStates.Failure("Email Not valid")
                }
            }catch (e: Exception){
                AuthRepository.AuthStates.Failure("Fail: ${e.message}")
            }
        return result
    }

    override suspend fun logOut(): AuthRepository.AuthStates {
        val result: AuthRepository.AuthStates =
            try {
                AuthRepository.AuthStates.Success("logOut Success")
            }catch (e:Exception){
                AuthRepository.AuthStates.Failure("logOut Fail: ${e.message}")
            }
        return result
    }

    override suspend fun resetPassword(email: String): AuthRepository.AuthStates {
        val result: AuthRepository.AuthStates =
            try {
                if (Utils.isEmailValid(email)){
                    auth.sendPasswordResetEmail(email).await()
                    AuthRepository.AuthStates.Success("Reset password Success")
                }else{
                    AuthRepository.AuthStates.Failure("Email not valid")
                }
            }catch (e: Exception){
                AuthRepository.AuthStates.Failure("Reset password failure: ${e.message}")
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