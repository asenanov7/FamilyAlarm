package com.example.familyalarm.domain.usecases.auth

import com.example.familyalarm.domain.repositories.AuthRepository
import com.example.familyalarm.utils.UiState

class LogOutUseCase(private val repository: AuthRepository) {
     suspend operator fun invoke(): UiState<Boolean> {
       return repository.logOut()
    }
}