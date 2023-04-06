package com.example.familyalarm.domain.usecases

import com.example.familyalarm.domain.entities.UserChild
import com.example.familyalarm.domain.repositories.Repository
import kotlinx.coroutines.flow.MutableSharedFlow

class GetUsersFromParentChildrensUseCase(private val repository: Repository) {
    operator fun invoke(parentId:String): MutableSharedFlow<List<UserChild>> {
        return repository.getUsersFromParentChildrens(parentId)
    }
}