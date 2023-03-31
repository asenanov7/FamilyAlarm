package com.example.familyalarm.presentation.viewmodels

import android.app.Application
import android.util.Log
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.example.familyalarm.data.impl_repositories.AuthRepositoryImpl
import com.example.familyalarm.utils.UiState
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ResetPasswordVM(application: Application): AndroidViewModel(application) {

    private val authRep = AuthRepositoryImpl(application)

    private val _stateFlow:MutableStateFlow<UiState<Boolean>> = MutableStateFlow(UiState.Init)
    val stateFlow:StateFlow<UiState<Boolean>>
    get() = _stateFlow.asStateFlow()

     suspend fun reset(email: String) {
        _stateFlow.value = UiState.Loading
        val result = authRep.resetPassword(email)
        _stateFlow.value = result
    }

    fun clearErrorsOnInputChanged(
        textInputEditTextEmail:TextInputEditText
    ){
        textInputEditTextEmail.addTextChangedListener{
            _stateFlow.value = UiState.Init
        }
    }





}
