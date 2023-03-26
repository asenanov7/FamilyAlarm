package com.example.familyalarm.domain.usecases.auth

import com.example.familyalarm.domain.repositories.AuthRepository
import com.example.familyalarm.domain.repositories.Repository
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.StateFlow

class LoginUseCase(private val repository: AuthRepository) {
     suspend operator fun invoke(email:String, password: String): AuthRepository.AuthStates {
       return repository.login(email, password)
    }
}