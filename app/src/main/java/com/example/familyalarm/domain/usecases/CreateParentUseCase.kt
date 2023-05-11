package com.example.familyalarm.domain.usecases

import com.example.familyalarm.domain.entities.UserParent
import com.example.familyalarm.domain.repositories.Repository

class CreateParentUseCase(private val repository: Repository) {
    suspend operator fun invoke(userParent: UserParent){
        repository.createParent(userParent)
    }
}