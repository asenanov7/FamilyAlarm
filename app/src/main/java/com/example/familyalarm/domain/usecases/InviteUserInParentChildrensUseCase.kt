package com.example.familyalarm.domain.usecases

import com.example.familyalarm.domain.repositories.Repository

class InviteUserInParentChildrensUseCase(private val repository: Repository) {
    suspend operator fun  invoke(userId: String, parentId: String) {
        repository.inviteUserInTheParentChildrens(userId, parentId)
    }
}