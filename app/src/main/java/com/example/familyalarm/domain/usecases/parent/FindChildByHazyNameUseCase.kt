package com.example.familyalarm.domain.usecases.parent

import com.example.familyalarm.domain.entities.UserChild
import com.example.familyalarm.domain.repositories.ParentRepository
import kotlinx.coroutines.flow.Flow

class FindChildByHazyNameUseCase(private val parentRepository: ParentRepository) {
    suspend operator fun invoke(name: String): Flow<List<UserChild>> {
       return parentRepository.findChildByHazyName(name)
    }
}