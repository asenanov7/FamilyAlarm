package com.example.familyalarm.data.impl_repositories

import com.example.familyalarm.data.Utils
import com.example.familyalarm.domain.repositories.AuthRepository
import com.example.familyalarm.domain.repositories.AuthRepository.AuthStates
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepositoryImpl : AuthRepository {

    private val auth by lazy { FirebaseAuth.getInstance() }


    override fun login(email: String, password: String): StateFlow<AuthStates> {
        val stateFlow: MutableStateFlow<AuthStates> = MutableStateFlow(AuthStates.Loading)
        if (Utils.isEmailValid(email)) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener { stateFlow.value = AuthStates.Success("Success") }
                .addOnFailureListener { stateFlow.value = AuthStates.Failure("${it.message}") }
        } else {
            stateFlow.value = AuthStates.Failure("Not valid")
        }
        return stateFlow.asStateFlow()
    }

    override fun register(email: String, password: String): StateFlow<AuthStates> {
        val stateFlow: MutableStateFlow<AuthStates> = MutableStateFlow(AuthStates.Loading)
        if (Utils.isEmailValid(email)) {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { stateFlow.value = AuthStates.Success("Success") }
                .addOnFailureListener { stateFlow.value = AuthStates.Failure("${it.message}") }
        } else {
            stateFlow.value = AuthStates.Failure("Not valid")
        }
        return stateFlow.asStateFlow()
    }

    override fun logOut(): StateFlow<AuthStates> {
        val stateFlow: MutableStateFlow<AuthStates> = MutableStateFlow(AuthStates.Loading)
        if (auth.currentUser!=null) {
            auth.signOut()
            while (auth.currentUser != null) {
                stateFlow.value = AuthStates.Loading
            }
            stateFlow.value = AuthStates.Success("SignOut: Success")
        }
        return stateFlow.asStateFlow()
    }

    override fun resetPassword(email: String): StateFlow<AuthStates> {
        val stateFlow: MutableStateFlow<AuthStates> = MutableStateFlow(AuthStates.Loading)
            if (Utils.isEmailValid(email)) {
                auth.sendPasswordResetEmail(email)
                    .addOnSuccessListener { stateFlow.value = AuthStates.Success("Reset: Success") }
                    .addOnFailureListener { stateFlow.value = AuthStates.Failure("Reset: Failure $it") }
            } else {
                stateFlow.value = AuthStates.Failure("Not valid")
            }
            return stateFlow.asStateFlow()
    }
}