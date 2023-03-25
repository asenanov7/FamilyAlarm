package com.example.familyalarm.domain.usecases

import com.example.familyalarm.domain.Repository
import com.example.familyalarm.domain.entities.User
import kotlinx.coroutines.flow.Flow

class GetUserInfoUseCase(private val repository: Repository) {
    operator fun invoke(userId:String): Flow<User> {
        return repository.getUserInfo(userId)
    }
}