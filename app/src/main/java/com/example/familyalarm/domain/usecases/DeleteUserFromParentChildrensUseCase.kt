package com.example.familyalarm.domain.usecases

import com.example.familyalarm.domain.repositories.Repository

class DeleteUserFromParentChildrensUseCase(private val repository: Repository) {
    operator fun invoke(userId: String, parentId: String){
        repository.deleteUserFromParentChildrens(userId, parentId)
    }
}