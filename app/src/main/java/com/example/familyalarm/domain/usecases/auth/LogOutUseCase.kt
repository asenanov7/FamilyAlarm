package com.example.familyalarm.domain.usecases.auth

import com.example.familyalarm.domain.repositories.AuthRepository
import kotlinx.coroutines.flow.StateFlow

class LogOutUseCase(private val repository: AuthRepository) {
     suspend operator fun invoke(): Boolean {
       return repository.logOut()
    }
}