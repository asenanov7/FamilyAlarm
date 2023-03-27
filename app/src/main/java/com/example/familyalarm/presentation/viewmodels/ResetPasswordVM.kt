package com.example.familyalarm.presentation.viewmodels

import android.util.Log
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModel
import com.example.familyalarm.data.impl_repositories.AuthRepositoryImpl
import com.example.familyalarm.presentation.viewmodels.ResetPasswordVMState.*
import com.example.familyalarm.utils.Utils
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ResetPasswordVM: ViewModel() {

    private val authRep = AuthRepositoryImpl()

    private val _stateFlow:MutableStateFlow<ResetPasswordVMState> = MutableStateFlow(Normal)
    val stateFlow:StateFlow<ResetPasswordVMState>
    get() = _stateFlow.asStateFlow()

    private suspend fun reset(email: String): Boolean {
        _stateFlow.value = Loading
        val result = authRep.resetPassword(email)
        return if (result){
            _stateFlow.value = Success
            true
        }else {
            _stateFlow.value = Failure
            false
        }
    }

    suspend fun checkValidAndReset(
        inpTextEmail: TextInputEditText,
        inpLayoutEmail: TextInputLayout
    ){
        inpTextEmail.addTextChangedListener{
            inpLayoutEmail.error = null
        }

        val email = inpTextEmail.text.toString().trim()
        if (!Utils.isEmailValid(email)){
            inpLayoutEmail.error = "Bad email"
        }
        if (inpLayoutEmail.error==null){
            reset(email)
            Log.d("ARSEN", "checkValidAndReset: reset ")
        }
    }



}

sealed class ResetPasswordVMState {
    object Normal : ResetPasswordVMState()
    object Loading : ResetPasswordVMState()
    object Success : ResetPasswordVMState()
    object Failure : ResetPasswordVMState()
}