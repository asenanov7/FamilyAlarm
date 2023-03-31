package com.example.familyalarm.presentation.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.familyalarm.data.impl_repositories.AuthRepositoryImpl
import com.example.familyalarm.domain.usecases.auth.LogOutUseCase
import com.example.familyalarm.domain.usecases.auth.RegisterUseCase
import com.example.familyalarm.utils.UiState
import com.example.familyalarm.utils.getErrorMessageFromFirebaseErrorCode
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainVM(application:Application):AndroidViewModel(application) {

    private val authRepository = AuthRepositoryImpl()
    private val logOutUseCase = LogOutUseCase(repository = authRepository)

    private val _stateFlow: MutableStateFlow<UiState<Boolean>> = MutableStateFlow(UiState.Init)
    val stateFlow: StateFlow<UiState<Boolean>>
        get() = _stateFlow.asStateFlow()

    suspend fun logOut(context: Context){
        _stateFlow.value = UiState.Loading
        val result: UiState<Boolean> =
            try {
                logOutUseCase()
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
        _stateFlow.value = result
    }
}