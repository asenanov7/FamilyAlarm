package com.example.familyalarm.domain.usecases

import com.example.familyalarm.domain.repositories.Repository
import com.example.familyalarm.domain.entities.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class GetUsersFromTheGroupUseCase(private val repository: Repository) {
    operator fun invoke(userGroupId:String): MutableSharedFlow<List<User>> {
        return repository.getUsersFromTheGroup(userGroupId)
    }
}