package com.example.familyalarm.domain.usecases

import com.example.familyalarm.domain.Repository
import kotlinx.coroutines.flow.StateFlow

class RegisterUseCase(private val repository: Repository) {
    operator fun invoke(email: String, password: String): StateFlow<Any> {
       return repository.register(email, password)
    }
}
