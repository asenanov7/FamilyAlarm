package com.example.familyalarm.domain

import kotlinx.coroutines.flow.Flow

class GetUsersFromTheGroupUseCase(private val repository: Repository) {
    operator fun invoke(userGroupId:String): Flow<List<User>> {
        return repository.getUsersFromTheGroup(userGroupId)
    }
}