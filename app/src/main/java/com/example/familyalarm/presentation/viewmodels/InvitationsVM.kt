package com.example.familyalarm.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.example.familyalarm.data.impl_repositories.RepositoryImpl
import com.example.familyalarm.domain.entities.UserChild
import com.example.familyalarm.domain.usecases.FindUserByHazyNameUseCase
import kotlinx.coroutines.flow.Flow

class InvitationsVM : ViewModel() {

    private val repositoryImpl = RepositoryImpl()
    private val findUsersByHazyNameUseCase = FindUserByHazyNameUseCase(repositoryImpl)

    suspend fun getUsersByHazyName(name: String): Flow<List<UserChild>> {
        return findUsersByHazyNameUseCase(name)
    }
}