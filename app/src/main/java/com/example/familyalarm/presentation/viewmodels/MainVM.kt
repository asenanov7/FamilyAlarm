package com.example.familyalarm.presentation.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.familyalarm.data.impl_repositories.AuthRepositoryImpl
import com.example.familyalarm.data.impl_repositories.RepositoryImpl
import com.example.familyalarm.domain.entities.UserChild
import com.example.familyalarm.domain.usecases.GetUsersFromParentChildrensUseCase
import com.example.familyalarm.domain.usecases.auth.LogOutUseCase
import com.example.familyalarm.utils.UiState
import com.example.familyalarm.utils.UiState.Failure
import com.example.familyalarm.utils.UiState.Success
import com.example.familyalarm.utils.getErrorMessageFromFirebaseErrorCode
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow

class MainVM(application:Application):AndroidViewModel(application) {

    private val authRepository = AuthRepositoryImpl()
    private val repositoryImpl = RepositoryImpl()
    private val logOutUseCase = LogOutUseCase(repository = authRepository)


    private val getUsersFromParentChildrensUseCase = GetUsersFromParentChildrensUseCase(repository = repositoryImpl)

   /* private val _stateFlow: MutableStateFlow<UiState<Boolean>> = MutableStateFlow(Default)
    val stateFlow: StateFlow<UiState<Boolean>>
        get() = _stateFlow.asStateFlow()*/


    fun getUsersFromParentChildrens(parentId:String): MutableSharedFlow<List<UserChild>> {
        return getUsersFromParentChildrensUseCase(parentId)
    }


    suspend fun logOut(context: Context) = flow{
                try {
                    logOutUseCase()
                    emit(Success(true))
                } catch (exception: Exception) {
                    when (exception) {
                        is FirebaseAuthException -> {
                            emit(Failure(
                                getErrorMessageFromFirebaseErrorCode(exception.errorCode, context)
                            ))
                        }
                        is FirebaseTooManyRequestsException -> {
                            emit(Failure("Слишком много попыток, пожалуйста попробуйте позже"))
                        }
                        else -> {
                            emit(Failure(
                                getErrorMessageFromFirebaseErrorCode(exception.message!!, context)
                            ))
                        }
                    }
                }
        }

    fun inviteUser(userid:String, groupId: String): Flow<UiState<Boolean>> = flow{
            emit(UiState.Loading)
            try {
                repositoryImpl.inviteUserInTheParentChildrens(
                    userid, groupId
                )
                emit(Success(true))
            } catch (e: Exception) {
                emit(Failure(e.message!!))
            }
    }
}