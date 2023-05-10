package com.example.familyalarm.presentation.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.flowWithLifecycle
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
import com.example.familyalarm.utils.UiState.*
import com.example.familyalarm.utils.getErrorMessageFromFirebaseErrorCode
import com.example.familyalarm.utils.throwEx
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainVM(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepositoryImpl()
    private val repositoryImpl = RepositoryImpl()

    private val logOutUseCase = LogOutUseCase(authRepository)
    private val getUserInfoUseCase = GetUserInfoUseCase(repositoryImpl)
    private val getUsersFromParentChildrensUseCase =
        GetUsersFromParentChildrensUseCase(repositoryImpl)
    private val deleteChildUseCase = DeleteUserFromCurrentParentUseCase(repositoryImpl)

    init {
        viewModelScope.launch { getChild() }
        repositoryImpl.setGeneralAutoChange()
        Log.d("setGeneralAutoChange", "setGeneralAutoChange")
    }

    private val _stateFlowListUserChild: MutableStateFlow<UiState<List<UserChild>>> =
        MutableStateFlow(Default)
    val stateFlowListUserChild: StateFlow<UiState<List<UserChild>>>
        get() = _stateFlowListUserChild.asStateFlow()

    private suspend fun getChild() {
        val user = getUserInfo()
        val parentId = user.id ?: throwEx(getChild())

        getUsersFromParentChildrensUseCase(parentId)
            .onStart { _stateFlowListUserChild.value = Loading }
            .onEach { _stateFlowListUserChild.value = Success(it) }
            .catch { _stateFlowListUserChild.value = Failure(it.message ?: "null message") }
            .launchIn(viewModelScope)
    }

    suspend fun getUserInfo(): User {
        val currentUserId = Firebase.auth.currentUser?.uid
            ?: throwEx(getUserInfo())
        return getUserInfoUseCase(currentUserId)
    }

    fun deleteChild(userId: String) {
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

}