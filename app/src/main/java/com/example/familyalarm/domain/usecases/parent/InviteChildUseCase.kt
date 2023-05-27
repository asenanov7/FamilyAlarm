package com.example.familyalarm.domain.usecases.parent

import com.example.familyalarm.domain.repositories.ParentRepository

class InviteChildUseCase(private val parentRepository: ParentRepository) {
    suspend operator fun  invoke(userId: String): Boolean {
        return parentRepository.inviteChild(userId)
    }
}