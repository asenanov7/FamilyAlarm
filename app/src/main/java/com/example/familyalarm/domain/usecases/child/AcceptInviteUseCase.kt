package com.example.familyalarm.domain.usecases.child

import com.example.familyalarm.domain.repositories.ChildRepository

class AcceptInviteUseCase(private val childRepository: ChildRepository) {
    suspend operator fun invoke(parentId: String) {
        childRepository.acceptInvite(parentId)
    }
}