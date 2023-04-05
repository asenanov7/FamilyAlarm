package com.example.familyalarm.domain.usecases

import com.example.familyalarm.domain.entities.UserChild
import com.example.familyalarm.domain.repositories.Repository
import kotlinx.coroutines.flow.MutableSharedFlow

class GetUsersFromTheGroupUseCase(private val repository: Repository) {
    operator fun invoke(userGroupId:String): MutableSharedFlow<List<UserChild>> {
        return repository.getUsersFromTheGroup(userGroupId)
    }
}