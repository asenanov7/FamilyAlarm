package com.example.familyalarm.domain.usecases

import com.example.familyalarm.data.impl_repositories.RepositoryImpl
import com.example.familyalarm.domain.repositories.Repository

class AcceptInviteUseCase(private val repository: Repository) {
    suspend operator fun invoke(parentId:String){
        repository.acceptInvite(parentId)
    }
}