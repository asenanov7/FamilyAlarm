package com.example.familyalarm.domain.usecases

import com.example.familyalarm.domain.repositories.Repository
import com.example.familyalarm.domain.entities.User
import kotlinx.coroutines.flow.Flow

class GetUserInfoUseCase(private val repository: Repository) {
    suspend operator fun invoke(userId:String): User {
        return repository.getUserInfo(userId)
    }
}