package com.example.familyalarm.presentation.viewmodels

import android.util.Log
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModel
import com.example.familyalarm.data.impl_repositories.AuthRepositoryImpl
import com.example.familyalarm.utils.UiState
import com.example.familyalarm.utils.UiState.*
import com.example.familyalarm.utils.Validation
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginVM : ViewModel() {

    private val authRepository = AuthRepositoryImpl()

    private val _stateFlow: MutableStateFlow<UiState<Boolean>> = MutableStateFlow(Init)
    val stateFlow: StateFlow<UiState<Boolean>>
    get() = _stateFlow.asStateFlow()

     suspend fun login(email: String, password: String) {
        _stateFlow.value = Loading
        val result = authRepository.login(email, password)
            _stateFlow.value = result
    }

    fun clearErrorOnInputChanged(
        textInputEditTextEmail: TextInputEditText,
        textInputEditTextPassword: TextInputEditText,
    ) {
        Log.d("SENANOV", "clearErrorsOnInputChanged: ")
        textInputEditTextEmail.addTextChangedListener {
            _stateFlow.value = Init
        }

        textInputEditTextPassword.addTextChangedListener {
            _stateFlow.value = Init
        }

    }


}



