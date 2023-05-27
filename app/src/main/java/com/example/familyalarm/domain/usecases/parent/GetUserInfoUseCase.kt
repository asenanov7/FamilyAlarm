package com.example.familyalarm.domain.usecases.parent

import com.example.familyalarm.domain.entities.User
import com.example.familyalarm.domain.repositories.GeneralRepository
import com.example.familyalarm.domain.repositories.ParentRepository

class GetUserInfoUseCase(private val generalRepository: GeneralRepository) {
    suspend operator fun invoke(userId:String): User {
        return generalRepository.getUserInfo(userId)
    }
}