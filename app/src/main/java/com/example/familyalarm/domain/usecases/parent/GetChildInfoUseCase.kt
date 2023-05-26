package com.example.familyalarm.domain.usecases.parent

import com.example.familyalarm.domain.entities.User
import com.example.familyalarm.domain.repositories.ParentRepository

class GetChildInfoUseCase(private val parentRepository: ParentRepository) {
    suspend operator fun invoke(userId:String): User {
        return parentRepository.getUserInfo(userId)
    }
}