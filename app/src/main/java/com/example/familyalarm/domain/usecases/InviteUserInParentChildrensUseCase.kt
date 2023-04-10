package com.example.familyalarm.domain.usecases

import com.example.familyalarm.domain.repositories.Repository
import kotlinx.coroutines.flow.Flow

class InviteUserInParentChildrensUseCase(private val repository: Repository) {
    suspend operator fun  invoke(userId: String): Boolean {
        return repository.inviteUserInTheParentChildrens(userId)
    }
}