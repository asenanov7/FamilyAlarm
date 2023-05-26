package com.example.familyalarm.domain.usecases.general

import com.example.familyalarm.domain.entities.UserParent
import com.example.familyalarm.domain.repositories.GeneralRepository

class CreateParentUseCase(private val generalRepository: GeneralRepository) {
    suspend operator fun invoke(userParent: UserParent){
        generalRepository.createParent(userParent)
    }
}