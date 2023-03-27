package com.example.familyalarm.presentation.viewmodels

import android.util.Log
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doBeforeTextChanged
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.familyalarm.data.impl_repositories.AuthRepositoryImpl
import com.example.familyalarm.utils.Utils
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.suspendCoroutine

class LoginVM : ViewModel() {

    private val authRepository = AuthRepositoryImpl()

    val stateFlow: MutableStateFlow<LoginVMState> = MutableStateFlow(LoginVMState.Normal)

    private suspend fun login(email: String, password: String) {
        Log.d("ARSEN", "login: ")
        stateFlow.value = LoginVMState.Loading
        val result = authRepository.login(email, password)
        if (result) {
            Log.d("ARSEN", "login: result = true")
            stateFlow.value = LoginVMState.Success
        } else {
            Log.d("ARSEN", "login: result = true")
            stateFlow.value = LoginVMState.Failure
        }
    }

    private fun checkErrorsAndSetNotifications(
        textInputEditTextEmail: TextInputEditText,
        textInputLayoutEmail: TextInputLayout,
        textInputEditTextPassword: TextInputEditText,
        textInputLayoutPassword: TextInputLayout,
    ): Boolean {
        if (!Utils.isEmailValid(textInputEditTextEmail.text.toString())) {
            textInputLayoutEmail.error = "Bad email"
            stateFlow.value = LoginVMState.Failure
        } else {
            textInputLayoutEmail.error = null
        }

        if (textInputEditTextPassword.text.toString().length < 7) {
            stateFlow.value = LoginVMState.Failure
            textInputLayoutPassword.error = "Short password"
        } else {
            textInputLayoutPassword.error = null
        }

        textInputEditTextEmail.addTextChangedListener {
            textInputLayoutEmail.error = null
        }

        textInputEditTextPassword.addTextChangedListener {
            textInputLayoutPassword.error = null
        }

        return !(textInputLayoutEmail.error != null || textInputLayoutPassword.error != null)
    }


    suspend fun checkErrorsAndLogin(
        textInputEditTextEmail: TextInputEditText,
        textInputLayoutEmail: TextInputLayout,
        textInputEditTextPassword: TextInputEditText,
        textInputLayoutPassword: TextInputLayout,
    ) {
        val valid = checkErrorsAndSetNotifications(
            textInputEditTextEmail,
            textInputLayoutEmail,
            textInputEditTextPassword,
            textInputLayoutPassword
        )
        if (valid) {
            login(textInputEditTextEmail.text.toString(), textInputEditTextPassword.text.toString())
        }
    }
}


    sealed class LoginVMState {

        object Normal : LoginVMState()
        object Loading : LoginVMState()
        object Success : LoginVMState()
        object Failure : LoginVMState()

    }


