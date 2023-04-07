package com.example.familyalarm.domain.usecases

import com.example.familyalarm.data.impl_repositories.RepositoryImpl
import com.example.familyalarm.domain.entities.UserChild
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class FindUserByHazyNameUseCase(private val repositoryImpl: RepositoryImpl) {
    suspend operator fun invoke(name: String): Flow<List<UserChild>> {
       return repositoryImpl.findUserByHazyName(name)
    }
}