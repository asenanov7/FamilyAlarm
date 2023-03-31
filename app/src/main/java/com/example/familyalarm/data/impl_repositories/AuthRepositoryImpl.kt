package com.example.familyalarm.data.impl_repositories

import android.util.Log
import com.example.familyalarm.domain.repositories.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl() : AuthRepository {

    private val auth by lazy { FirebaseAuth.getInstance() }

    override suspend fun login(email: String, password: String) {
        Log.d("SENANOV", "login: IMPL LOGIN")
        auth.signInWithEmailAndPassword(email, password).await()

    }

    override suspend fun register(email: String, password: String) {
        Log.d("SENANOV", "register: IMPL REGISTER")
        auth.createUserWithEmailAndPassword(email, password).await()
    }

    override suspend fun logOut() { //Сделать
        Log.d("SENANOV", "logOut: IMPL logOut")
        if (auth.currentUser != null) {
            auth.signOut()
        } else {
            throw Exception("currentUser = null")
        }
    }

    override suspend fun resetPassword(email: String) {
        Log.d("SENANOV", "resetPassword: IMPL resetPassword")
        auth.sendPasswordResetEmail(email).await()
    }

}