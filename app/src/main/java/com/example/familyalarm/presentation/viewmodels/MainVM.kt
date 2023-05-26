package com.example.familyalarm.presentation.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.familyalarm.data.impl_repositories.AuthRepositoryImpl
import com.example.familyalarm.data.impl_repositories.GeneralRepositoryImpl
import com.example.familyalarm.data.impl_repositories.ParentRepositoryImpl
import com.example.familyalarm.data.listeners.SingleFirebaseListener
import com.example.familyalarm.domain.entities.User
import com.example.familyalarm.domain.entities.UserChild
import com.example.familyalarm.domain.usecases.auth.LogOutUseCase
import com.example.familyalarm.domain.usecases.parent.DeleteChild
import com.example.familyalarm.domain.usecases.parent.GetUserInfoUseCase
import com.example.familyalarm.domain.usecases.parent.GetParentChildsUseCase
import com.example.familyalarm.utils.UiState
import com.example.familyalarm.utils.UiState.*
import com.example.familyalarm.utils.getErrorMessageFromFirebaseErrorCode
import com.example.familyalarm.utils.throwEx
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

class MainVM(application: Application) : AndroidViewModel(application) {

    private val logOutUseCase = LogOutUseCase(AuthRepositoryImpl)
    private val getUserInfoUseCase = GetUserInfoUseCase(GeneralRepositoryImpl)
    private val getParentChildsUseCase = GetParentChildsUseCase(ParentRepositoryImpl)
    private val deleteChildUseCase = DeleteChild(ParentRepositoryImpl)



    init {
        GeneralRepositoryImpl.setGeneralAutoChangeListener()
        Log.d("setGeneralAutoChange", "setGeneralAutoChange")
    }


    suspend fun getChilds(): Flow<UiState<List<UserChild>>> {
        return getParentChildsUseCase()
            .map { Success(it) as UiState<List<UserChild>> }
            .onStart { emit(Loading) }
            .retry(2) { delay(1000) ; true }
            .catch { emit(Failure(it.message ?: "null message")) }
    }


    suspend fun getUserInfo(id:String): User {
        return getUserInfoUseCase(id)
    }

    fun deleteChild(userId: String) {
        Firebase.auth.currentUser?.let {
            deleteChildUseCase(userId, it.uid)
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