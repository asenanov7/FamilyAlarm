package com.example.familyalarm.domain.usecases

import com.example.familyalarm.domain.repositories.Repository

class InviteUserInTheGroupUseCase(private val repository: Repository) {
    operator fun invoke(userId: String, userGroupId: String) {
        repository.inviteUserInTheGroup(userId, userGroupId)
    }
}