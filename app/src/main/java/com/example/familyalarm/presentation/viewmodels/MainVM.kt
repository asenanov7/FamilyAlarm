package com.example.familyalarm.presentation.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.familyalarm.data.impl_repositories.AuthRepositoryImpl
import com.example.familyalarm.data.impl_repositories.RepositoryImpl
import com.example.familyalarm.domain.entities.User
import com.example.familyalarm.domain.entities.UserChild
import com.example.familyalarm.domain.usecases.DeleteUserFromCurrentParentUseCase
import com.example.familyalarm.domain.usecases.GetUserInfoUseCase
import com.example.familyalarm.domain.usecases.GetUsersFromParentChildrensUseCase
import com.example.familyalarm.domain.usecases.auth.LogOutUseCase
import com.example.familyalarm.utils.UiState
import com.example.familyalarm.utils.UiState.Failure
import com.example.familyalarm.utils.UiState.Success
import com.example.familyalarm.utils.getErrorMessageFromFirebaseErrorCode
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext

class MainVM(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepositoryImpl()
    private val repositoryImpl = RepositoryImpl()

    private val logOutUseCase = LogOutUseCase(authRepository)
    private val getUserInfoUseCase = GetUserInfoUseCase(repositoryImpl)
    private val getUsersFromParentChildrensUseCase = GetUsersFromParentChildrensUseCase(repositoryImpl)
    private val deleteChildUseCase = DeleteUserFromCurrentParentUseCase(repositoryImpl)

    init {
        repositoryImpl.setGeneralAutoChange()
        Log.d("setGeneralAutoChange", "setGeneralAutoChange")
    }

    suspend fun getUserInfo(): User {
        val currentUserId = Firebase.auth.currentUser!!.uid
        return getUserInfoUseCase(currentUserId)
    }

    fun deleteChild(userId:String){
        val parentId = Firebase.auth.currentUser?.let {
            deleteChildUseCase(userId, it.uid)
        }
    }

    fun getUsersFromParentChildrens(parentId: String): MutableSharedFlow<List<UserChild>> {
        return getUsersFromParentChildrensUseCase(parentId)
    }


    suspend fun logOut(context: Context) = flow {
        try {
            logOutUseCase()
            emit(Success(true))
        } catch (exception: Exception) {
            when (exception) {
                is FirebaseAuthException -> {
                    emit(
                        Failure(
                            getErrorMessageFromFirebaseErrorCode(exception.errorCode, context)
                        )
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

}