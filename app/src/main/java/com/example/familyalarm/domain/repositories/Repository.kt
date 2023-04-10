package com.example.familyalarm.domain.repositories

import com.example.familyalarm.domain.entities.User
import com.example.familyalarm.domain.entities.UserChild
import com.example.familyalarm.domain.entities.UserParent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

interface Repository {

    suspend fun createChildUseCase(userChild: UserChild)

    suspend fun createParentUseCase(userParent: UserParent)

    suspend fun inviteUserInTheParentChildrens(userId: String): Boolean

    suspend fun findUserByHazyName(name:String): Flow<List<UserChild>>

    fun deleteUserFromParentChildrens(userId: String, parentId: String)

    fun getUsersFromParentChildrens(parentId: String): MutableSharedFlow<List<UserChild>>

    fun getUserInfo(userId: String): Flow<User>

}