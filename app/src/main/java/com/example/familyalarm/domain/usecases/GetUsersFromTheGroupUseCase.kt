package com.example.familyalarm.domain.usecases

import com.example.familyalarm.domain.repositories.Repository
import com.example.familyalarm.domain.entities.User
import kotlinx.coroutines.flow.Flow

class GetUsersFromTheGroupUseCase(private val repository: Repository) {
    operator fun invoke(userGroupId:String): Flow<List<User>> {
        return repository.getUsersFromTheGroup(userGroupId)
    }
}