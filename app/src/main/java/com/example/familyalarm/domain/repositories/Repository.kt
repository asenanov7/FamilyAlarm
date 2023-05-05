package com.example.familyalarm.domain.repositories

import com.example.familyalarm.domain.entities.User
import com.example.familyalarm.domain.entities.UserChild
import com.example.familyalarm.domain.entities.UserParent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

interface Repository {

    suspend fun createChildUseCase(userChild: UserChild)

    suspend fun createParentUseCase(userParent: UserParent)

    suspend fun inviteUserInTheParentChildrens(userId: String): Boolean

    suspend fun getInvitations(userId: String): MutableSharedFlow<List<UserParent>>

    suspend fun acceptInvite(parentId: String)

    suspend fun findUserByHazyName(name:String): Flow<List<UserChild>>

    fun deleteUserFromCurrentParent(userId: String, parentId: String)

    fun getUsersFromParentChildrens(parentId: String): Flow<List<UserChild>>

    suspend fun getUserInfo(userId: String): User

}