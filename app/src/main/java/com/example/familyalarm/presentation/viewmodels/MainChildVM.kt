package com.example.familyalarm.presentation.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.familyalarm.data.impl_repositories.AuthRepositoryImpl
import com.example.familyalarm.data.impl_repositories.ChildRepositoryImpl
import com.example.familyalarm.data.listeners.SingleFirebaseListener
import com.example.familyalarm.domain.entities.UserChild
import com.example.familyalarm.domain.usecases.auth.LogOutUseCase
import com.example.familyalarm.domain.usecases.child.GetChildsForChildUseCase
import com.example.familyalarm.utils.UiState
import com.example.familyalarm.utils.getErrorMessageFromFirebaseErrorCode
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

class MainChildVM : ViewModel() {
    private val authRepository = AuthRepositoryImpl
    private val childRepositoryImpl = ChildRepositoryImpl

    private val logOutUseCase = LogOutUseCase(authRepository)
    private val childGetUseCase = GetChildsForChildUseCase(childRepositoryImpl)

    suspend fun getChilds(): Flow<UiState<List<UserChild>>> {
        return childGetUseCase()
            .map { UiState.Success(it) as UiState<List<UserChild>> }
            .onStart { emit(UiState.Loading) }
            .retry(2) { delay(1000) ; true }
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

    fun detachAllListeners(){
        object : SingleFirebaseListener<UserChild>(){
            override fun onDataChangeCustom(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onCanceledCustom(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }.apply {
            detachAllListeners()
        }
    }
}