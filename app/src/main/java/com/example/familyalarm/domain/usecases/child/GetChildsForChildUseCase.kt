package com.example.familyalarm.domain.usecases.child

import com.example.familyalarm.domain.entities.UserChild
import com.example.familyalarm.domain.repositories.ChildRepository
import kotlinx.coroutines.flow.Flow

class GetChildsForChildUseCase(private val childRepository: ChildRepository) {
    suspend operator fun invoke(): Flow<List<UserChild>> {
        return childRepository.getChilds
    }
}