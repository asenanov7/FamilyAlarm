package com.example.familyalarm.domain.repositories

import com.example.familyalarm.domain.entities.User
import com.example.familyalarm.domain.entities.UserChild
import com.example.familyalarm.domain.entities.UserParent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

interface Repository {

    suspend fun createChildUseCase(userChild:UserChild)

    suspend fun createParentUseCase(userParent:UserParent)

    fun inviteUserInTheGroup(userId: String, userGroupId: String)

    fun deleteUserFromGroup(userId: String, userGroupId: String)

    fun getUsersFromTheGroup(userGroupId: String): MutableSharedFlow<List<UserChild>>

    fun getUserInfo(userId: String): Flow<User>

}