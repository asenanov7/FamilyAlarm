package com.example.familyalarm.presentation.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.familyalarm.data.impl_repositories.AuthRepositoryImpl
import com.example.familyalarm.data.impl_repositories.ChildRepositoryImpl
import com.example.familyalarm.data.impl_repositories.GeneralRepositoryImpl
import com.example.familyalarm.data.impl_repositories.ParentRepositoryImpl
import com.example.familyalarm.domain.entities.UserChild
import com.example.familyalarm.domain.usecases.auth.LogOutUseCase
import com.example.familyalarm.domain.usecases.child.GetChildsForChildUseCase
import com.example.familyalarm.utils.UiState
import com.example.familyalarm.utils.getErrorMessageFromFirebaseErrorCode
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainChildVM : ViewModel() {


    private val logOutUseCase = LogOutUseCase(AuthRepositoryImpl)
    private val childGetUseCase = GetChildsForChildUseCase(ChildRepositoryImpl.create())

    suspend fun getChilds(): Flow<UiState<List<UserChild>>> {
        return childGetUseCase()
            .map { UiState.Success(it) as UiState<List<UserChild>> }
            .onStart { emit(UiState.Loading) }
            .retry(2) { delay(1000); true }
            .catch { emit(UiState.Failure(it.message ?: "null message")) }
    }

    suspend fun logOut(context: Context) = flow {
        try {
            logOutUseCase()
            emit(UiState.Success(true))
        } catch (exception: Exception) {
            when (exception) {
                is FirebaseAuthException -> {
                    emit(
                        UiState.Failure(
                            getErrorMessageFromFirebaseErrorCode(
                                exception.errorCode,
                                context
                            )
                        )
                    )
                }
                is FirebaseTooManyRequestsException -> {
                    emit(UiState.Failure("Слишком много попыток, пожалуйста попробуйте позже"))
                }
                else -> {
                    emit(
                        UiState.Failure(
                            getErrorMessageFromFirebaseErrorCode(exception.message!!, context)
                        )
                    )
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()

        ChildRepositoryImpl.destroy()
        GeneralRepositoryImpl.destroy()

    }
}