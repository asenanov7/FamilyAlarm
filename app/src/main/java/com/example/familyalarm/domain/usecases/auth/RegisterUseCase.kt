package com.example.familyalarm.domain.usecases.auth

import com.example.familyalarm.domain.repositories.AuthRepository
import com.example.familyalarm.utils.UiState

class RegisterUseCase(private val repository: AuthRepository) {
     suspend operator fun invoke(email: String, password: String) {
       return repository.register(email, password)
    }
}
