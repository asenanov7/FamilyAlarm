package com.example.familyalarm.domain.usecases.parent

import com.example.familyalarm.domain.entities.UserChild
import com.example.familyalarm.domain.repositories.ParentRepository
import kotlinx.coroutines.flow.Flow

class GetParentChildsUseCase(private val parentRepository: ParentRepository) {
    operator fun invoke(): Flow<List<UserChild>> {
        return parentRepository.childsFLOW
    }
}