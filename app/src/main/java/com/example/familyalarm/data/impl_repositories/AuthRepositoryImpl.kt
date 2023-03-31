package com.example.familyalarm.data.impl_repositories

import android.content.Context
import android.util.Log
import com.example.familyalarm.domain.repositories.AuthRepository
import com.example.familyalarm.utils.UiState
import com.example.familyalarm.utils.getErrorMessageFromFirebaseErrorCode
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(private val context: Context) : AuthRepository {

    private val auth by lazy { FirebaseAuth.getInstance() }

    override suspend fun login(email: String, password: String): UiState<Boolean> {
        Log.d("SENANOV", "login: IMPL LOGIN")
        val result: UiState<Boolean> =
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                UiState.Success(true)
            } catch (exception: Exception) {
                when (exception) {
                    is FirebaseAuthException -> {
                        UiState.Failure(
                            getErrorMessageFromFirebaseErrorCode(exception.errorCode, context)
                        )
                    }
                    is FirebaseTooManyRequestsException -> {
                        UiState.Failure("Слишком много попыток, пожалуйста попробуйте позже")
                    }
                    else -> {
                        UiState.Failure(
                            getErrorMessageFromFirebaseErrorCode(exception.message!!, context)
                        )
                    }
                }
            }
        return result
    }

    override suspend fun register(email: String, password: String): UiState<Boolean> {
        Log.d("SENANOV", "register: IMPL REGISTER")
        val result: UiState<Boolean> =
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                UiState.Success(true)
            } catch (exception: Exception) {
                when (exception) {
                    is FirebaseAuthException -> {
                        UiState.Failure(
                            getErrorMessageFromFirebaseErrorCode(exception.errorCode, context)
                        )
                    }
                    is FirebaseTooManyRequestsException -> {
                        UiState.Failure("Слишком много попыток, пожалуйста попробуйте позже")
                    }
                    else -> {
                        UiState.Failure(
                            getErrorMessageFromFirebaseErrorCode(exception.message!!, context)
                        )
                    }
                }
            }
        return result
    }

    override suspend fun logOut(): UiState<Boolean> {
        val result: UiState<Boolean> =
            try {
                if (auth.currentUser != null) {
                    auth.signOut()
                    UiState.Success(true)
                } else {
                    throw Exception("currentUser = null")
                }
            } catch (exception: Exception) {
                when (exception) {
                    is FirebaseAuthException -> {
                        UiState.Failure(
                            getErrorMessageFromFirebaseErrorCode(exception.errorCode, context)
                        )
                    }
                    is FirebaseTooManyRequestsException -> {
                        UiState.Failure("Слишком много попыток, пожалуйста попробуйте позже")
                    }
                    else -> {
                        UiState.Failure(
                            getErrorMessageFromFirebaseErrorCode(exception.message!!, context)
                        )
                    }
                }
            }
        return result
    }

    override suspend fun resetPassword(email: String): UiState<Boolean> {
        val result: UiState<Boolean> =
            try {
                auth.sendPasswordResetEmail(email).await()
                UiState.Success(true)
            } catch (exception: Exception) {
                when (exception) {
                    is FirebaseAuthException -> {
                        UiState.Failure(
                            getErrorMessageFromFirebaseErrorCode(exception.errorCode, context)
                        )
                    }
                    is FirebaseTooManyRequestsException -> {
                        UiState.Failure("Слишком много попыток, пожалуйста попробуйте позже")
                    }
                    else -> {
                        UiState.Failure(
                            getErrorMessageFromFirebaseErrorCode(exception.message!!, context)
                        )
                    }
                }
            }
        return result
    }

}