package com.example.familyalarm.domain.usecases

import com.example.familyalarm.domain.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class LoginUseCase(private val repository: Repository) {
    operator fun invoke(email:String, password: String): StateFlow<Any> {
       return repository.login(email, password)
    }
}