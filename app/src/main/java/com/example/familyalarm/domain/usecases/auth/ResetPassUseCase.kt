package com.example.familyalarm.domain.usecases.auth

import com.example.familyalarm.domain.repositories.AuthRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.StateFlow

class ResetPassUseCase(private val repository: AuthRepository) {
     operator fun invoke(email:String): StateFlow<AuthRepository.AuthStates> {
        return repository.resetPassword(email)
    }
}