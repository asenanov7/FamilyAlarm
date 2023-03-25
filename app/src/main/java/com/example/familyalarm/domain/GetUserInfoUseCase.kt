package com.example.familyalarm.domain

import kotlinx.coroutines.flow.Flow

class GetUserInfoUseCase(private val repository: Repository) {
    operator fun invoke(userId:String): Flow<User> {
        return repository.getUserInfo(userId)
    }
}