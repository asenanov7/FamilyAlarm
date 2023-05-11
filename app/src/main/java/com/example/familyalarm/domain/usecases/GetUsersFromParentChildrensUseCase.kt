package com.example.familyalarm.domain.usecases

import com.example.familyalarm.domain.entities.UserChild
import com.example.familyalarm.domain.repositories.Repository
import kotlinx.coroutines.flow.Flow

class GetUsersFromParentChildrensUseCase(private val repository: Repository) {
    operator fun invoke(parentId:String): Flow<List<UserChild>> {
        return repository.getChilds(parentId)
    }
}