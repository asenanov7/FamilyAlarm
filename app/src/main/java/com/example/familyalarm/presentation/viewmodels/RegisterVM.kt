package com.example.familyalarm.presentation.viewmodels

import android.app.Application
import android.content.Context
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.example.familyalarm.data.impl_repositories.AuthRepositoryImpl
import com.example.familyalarm.domain.usecases.auth.RegisterUseCase
import com.example.familyalarm.utils.UiState
import com.example.familyalarm.utils.UiState.*
import com.example.familyalarm.utils.getErrorMessageFromFirebaseErrorCode
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

class RegisterVM(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepositoryImpl()
    private val registerUseCase = RegisterUseCase(repository = authRepository)

    private val _stateFlow: MutableStateFlow<UiState<Boolean>> = MutableStateFlow(Init)
    val stateFlow: StateFlow<UiState<Boolean>>
        get() = _stateFlow.asStateFlow()

    suspend fun register(name: String, email: String, password: String, context: Context) {
        _stateFlow.value = Loading
        val result: UiState<Boolean> =
            try {
                registerUseCase(email, password)
                Success(true)
            } catch (exception: Exception) {
                when (exception) {
                    is FirebaseAuthException -> {
                        Failure(
                            getErrorMessageFromFirebaseErrorCode(exception.errorCode, context)
                        )
                    }
                    is FirebaseTooManyRequestsException -> {
                        Failure("Слишком много попыток, пожалуйста попробуйте позже")
                    }
                    else -> {
                        Failure(
                            getErrorMessageFromFirebaseErrorCode(exception.message!!, context)
                        )
                    }
                }
            }
        _stateFlow.value = result
    }


}