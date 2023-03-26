package com.example.familyalarm.domain.usecases.auth

import com.example.familyalarm.domain.repositories.AuthRepository
import com.example.familyalarm.domain.repositories.Repository
import kotlinx.coroutines.flow.StateFlow

class LoginUseCase(private val repository: AuthRepository) {
     operator fun invoke(email:String, password: String): StateFlow<AuthRepository.AuthStates> {
       return repository.login(email, password)
    }
}