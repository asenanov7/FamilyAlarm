package com.example.familyalarm.presentation.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.familyalarm.data.impl_repositories.AuthRepositoryImpl
import com.example.familyalarm.domain.usecases.auth.ResetPassUseCase
import com.example.familyalarm.utils.UiState
import com.example.familyalarm.utils.UiState.*
import com.example.familyalarm.utils.getErrorMessageFromFirebaseErrorCode
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ResetPasswordVM(application: Application): AndroidViewModel(application) {

    private val authRep = AuthRepositoryImpl()
    private val resetUseCase = ResetPassUseCase(repository = authRep)

    private val _stateFlow:MutableStateFlow<UiState<Boolean>> = MutableStateFlow(Default)
    val stateFlow:StateFlow<UiState<Boolean>>
    get() = _stateFlow.asStateFlow()

     suspend fun reset(email: String, context: Context) {
         _stateFlow.value = Loading
         val result: UiState<Boolean> =
             try {
                 resetUseCase(email)
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
