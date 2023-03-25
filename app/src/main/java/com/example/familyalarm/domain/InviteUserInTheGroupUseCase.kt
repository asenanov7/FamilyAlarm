package com.example.familyalarm.domain

class InviteUserInTheGroupUseCase(private val repository: Repository) {
    operator fun invoke(userId: String, userGroupId: String) {
        repository.inviteUserInTheGroup(userId, userGroupId)
    }
}