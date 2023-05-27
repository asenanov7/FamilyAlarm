package com.example.familyalarm.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.example.familyalarm.data.impl_repositories.ChildRepositoryImpl
import com.example.familyalarm.data.impl_repositories.GeneralRepositoryImpl
import com.example.familyalarm.domain.entities.UserParent
import com.example.familyalarm.domain.usecases.child.AcceptInviteUseCase
import com.example.familyalarm.domain.usecases.child.GetInvitationsUseCase
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class InvitationVM:ViewModel() {

    private val childRepositoryImpl = ChildRepositoryImpl
    private val getInvitationsUseCase = GetInvitationsUseCase(childRepositoryImpl)
    private val acceptInviteUseCase = AcceptInviteUseCase(childRepositoryImpl)

    suspend fun invitations(): Flow<List<UserParent>> {
        val childId  = Firebase.auth.currentUser!!.uid
        return getInvitationsUseCase(childId)
    }

    suspend fun accept(parentId:String){
          return acceptInviteUseCase(parentId)
    }
}