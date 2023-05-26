package com.example.familyalarm.domain.usecases.child

import com.example.familyalarm.domain.entities.UserParent
import com.example.familyalarm.domain.repositories.ChildRepository
import kotlinx.coroutines.flow.Flow

class GetInvitationsUseCase(private val childRepository: ChildRepository) {
    suspend operator fun invoke(childId:String): Flow<List<UserParent>> {
        return childRepository.getInvitations(childId)
    }
}