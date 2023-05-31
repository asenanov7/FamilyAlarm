package com.example.familyalarm.presentation.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.familyalarm.data.impl_repositories.AuthRepositoryImpl
import com.example.familyalarm.data.impl_repositories.ChildRepositoryImpl
import com.example.familyalarm.data.impl_repositories.GeneralRepositoryImpl
import com.example.familyalarm.data.impl_repositories.ParentRepositoryImpl
import com.example.familyalarm.domain.entities.User
import com.example.familyalarm.domain.entities.UserChild
import com.example.familyalarm.domain.usecases.auth.LogOutUseCase
import com.example.familyalarm.domain.usecases.parent.DeleteChild
import com.example.familyalarm.domain.usecases.parent.GetParentChildsUseCase
import com.example.familyalarm.domain.usecases.parent.GetUserInfoUseCase
import com.example.familyalarm.utils.UiState
import com.example.familyalarm.utils.UiState.*
import com.example.familyalarm.utils.getErrorMessageFromFirebaseErrorCode
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainVM(application: Application) : AndroidViewModel(application) {

    private val generalRepositoryImpl = GeneralRepositoryImpl.create()
    private val parentRepositoryImpl = ParentRepositoryImpl.create()

    private val logOutUseCase = LogOutUseCase(AuthRepositoryImpl)
    private val getUserInfoUseCase = GetUserInfoUseCase(generalRepositoryImpl)
    private val getParentChildsUseCase = GetParentChildsUseCase(parentRepositoryImpl)
    private val deleteChildUseCase = DeleteChild(parentRepositoryImpl)


    init {
        generalRepositoryImpl.setGeneralAutoChangeListener()
        Log.d("setGeneralAutoChange", "setGeneralAutoChange")
    }


    suspend fun getChilds(): Flow<UiState<List<UserChild>>> {
        return getParentChildsUseCase()
            .map { Success(it) as UiState<List<UserChild>> }
            .onStart { emit(Loading) }
            .retry(2) { delay(1000); true }
            .catch { emit(Failure(it.message ?: "null message")) }
    }


    suspend fun getUserInfo(id: String): User {
        return getUserInfoUseCase(id)
    }

    suspend fun deleteChild(userId: String) {
        Firebase.auth.currentUser?.let {
            deleteChildUseCase(userId, it.uid)
        }
    }


    suspend fun logOut(context: Context) = flow {
        try {
            logOutUseCase()
            emit(Success(true))
        } catch (exception: Exception) {
            when (exception) {
                is FirebaseAuthException -> {
                    emit(
                        Failure(getErrorMessageFromFirebaseErrorCode(exception.errorCode, context))
                    )
                }
                is FirebaseTooManyRequestsException -> {
                    emit(Failure("Слишком много попыток, пожалуйста попробуйте позже"))
                }
                else -> {
                    emit(
                        Failure(
                            getErrorMessageFromFirebaseErrorCode(exception.message!!, context)
                        )
                    )
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()

        ParentRepositoryImpl.destroy()
        GeneralRepositoryImpl.destroy()

    }


}