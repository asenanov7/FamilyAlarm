package com.example.familyalarm.domain.usecases

import com.example.familyalarm.domain.entities.UserChild
import com.example.familyalarm.domain.repositories.Repository

class CreateChildUseCase(private val repository: Repository) {
    suspend operator fun invoke(userChild: UserChild){
        repository.createChild(userChild)
    }
}