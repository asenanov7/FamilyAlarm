package com.example.familyalarm.domain.usecases.auth

import com.example.familyalarm.domain.repositories.AuthRepository
import com.example.familyalarm.domain.repositories.Repository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class RegisterUseCase(private val repository: AuthRepository) {
     operator fun invoke(email: String, password: String): StateFlow<AuthRepository.AuthStates> {
       return repository.register(email, password)
    }
}
