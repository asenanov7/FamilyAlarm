package com.example.familyalarm.domain.usecases.auth

import com.example.familyalarm.domain.repositories.AuthRepository
import com.example.familyalarm.utils.UiState

class ResetPassUseCase(private val repository: AuthRepository) {
     suspend operator fun invoke(email:String) {
        return repository.resetPassword(email)
    }
}