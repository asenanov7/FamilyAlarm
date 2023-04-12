package com.example.familyalarm.domain.usecases

import com.example.familyalarm.domain.entities.UserParent
import com.example.familyalarm.domain.repositories.Repository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class GetInvitationsUseCase(private val repository: Repository) {
    suspend operator fun invoke(childId:String):  MutableSharedFlow<List<UserParent>> {
        return repository.getInvitations(childId)
    }
}