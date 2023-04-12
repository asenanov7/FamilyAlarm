package com.example.familyalarm.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.example.familyalarm.data.impl_repositories.RepositoryImpl
import com.example.familyalarm.domain.entities.UserParent
import com.example.familyalarm.domain.usecases.AcceptInviteUseCase
import com.example.familyalarm.domain.usecases.GetInvitationsUseCase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableSharedFlow

class InvitationVM:ViewModel() {

    private val repositoryImpl = RepositoryImpl()
    private val getInvitationsUseCase = GetInvitationsUseCase(repositoryImpl)
    private val acceptInviteUseCase = AcceptInviteUseCase(repositoryImpl)

    suspend fun invitations(): MutableSharedFlow<List<UserParent>> {
        val childId  = Firebase.auth.currentUser!!.uid
        return getInvitationsUseCase(childId)
    }

    suspend fun accept(parentId:String){
          return repositoryImpl.acceptInvite(parentId)
    }
}