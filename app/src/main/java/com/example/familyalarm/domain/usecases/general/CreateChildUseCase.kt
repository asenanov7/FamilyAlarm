package com.example.familyalarm.domain.usecases.general

import com.example.familyalarm.domain.entities.UserChild
import com.example.familyalarm.domain.repositories.GeneralRepository

class CreateChildUseCase(private val generalRepository: GeneralRepository) {
    suspend operator fun invoke(userChild: UserChild){
        generalRepository.createChild(userChild)
    }
}