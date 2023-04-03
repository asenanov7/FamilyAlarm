package com.example.familyalarm.presentation.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.familyalarm.data.impl_repositories.AuthRepositoryImpl
import com.example.familyalarm.data.impl_repositories.RepositoryImpl
import com.example.familyalarm.domain.entities.User
import com.example.familyalarm.domain.usecases.CreateUserUseCase
import com.example.familyalarm.domain.usecases.auth.RegisterUseCase
import com.example.familyalarm.utils.UiState
import com.example.familyalarm.utils.UiState.*
import com.example.familyalarm.utils.getErrorMessageFromFirebaseErrorCode
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class RegisterVM(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepositoryImpl()
    private val repositoryImpl = RepositoryImpl()
    private val registerUseCase = RegisterUseCase(repository = authRepository)
    private val createUserUseCase = CreateUserUseCase(repository = repositoryImpl)
    private val auth = FirebaseAuth.getInstance()

    private val _stateFlow: MutableStateFlow<UiState<Boolean>> = MutableStateFlow(Default)
    val stateFlow: StateFlow<UiState<Boolean>>
        get() = _stateFlow.asStateFlow()

     suspend fun registerAndCreateUser(name:String, email:String,password: String,context: Context){
        _stateFlow.value = Loading

        val result:UiState<Boolean> =
            try {
                registerUseCase(email,password)
                auth.currentUser?.let {
                    val user = User(
                        id = it.uid,
                        name = name,
                        email = email,
                        password = password,
                        personalGroupId = it.uid,
                        currentGroupId = it.uid,
                    )
                    createUserUseCase(user)
                }
                Success(true)
            }catch (exception:Exception){
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