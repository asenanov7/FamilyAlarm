package com.example.familyalarm.domain

class DeleteUserFromGroupUseCase(private val repository: Repository) {
    operator fun invoke(userId: String, userGroupId: String){
        repository.deleteUserFromGroup(userId, userGroupId)
    }
}