package com.example.familyalarm.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.example.familyalarm.data.impl_repositories.ChildRepositoryImpl
import com.example.familyalarm.data.impl_repositories.GeneralRepositoryImpl
import com.example.familyalarm.data.impl_repositories.ParentRepositoryImpl
import com.example.familyalarm.domain.entities.UserChild
import com.example.familyalarm.domain.usecases.parent.FindChildByHazyNameUseCase
import com.example.familyalarm.domain.usecases.parent.InviteChildUseCase
import kotlinx.coroutines.flow.Flow

class SearchVM : ViewModel() {

    private val parentRepositoryImpl = ParentRepositoryImpl
    private val findUsersByHazyNameUseCase = FindChildByHazyNameUseCase(parentRepositoryImpl)
    private val inviteChildUseCase = InviteChildUseCase(parentRepositoryImpl)

    suspend fun getUsersByHazyName(name: String): Flow<List<UserChild>> {
        return findUsersByHazyNameUseCase(name)
    }

    suspend fun invite(userId: String): Boolean{
        return inviteChildUseCase(userId)
    }
}