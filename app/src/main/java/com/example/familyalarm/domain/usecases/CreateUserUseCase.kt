package com.example.familyalarm.domain.usecases

import com.example.familyalarm.domain.entities.User
import com.example.familyalarm.domain.repositories.Repository

class CreateUserUseCase(private val repository: Repository) {
    operator fun invoke(user: User){
        repository.createUserUseCase(user)
    }
}