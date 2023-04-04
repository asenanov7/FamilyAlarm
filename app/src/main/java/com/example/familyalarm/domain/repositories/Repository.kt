package com.example.familyalarm.domain.repositories

import com.example.familyalarm.domain.entities.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

interface Repository {

    fun createUserUseCase(user:User)

    fun inviteUserInTheGroup(userId: String, userGroupId: String)

    fun deleteUserFromGroup(userId: String, userGroupId: String)

    fun getUsersFromTheGroup(userGroupId: String): MutableSharedFlow<List<User>>

    fun getUserInfo(userId: String): Flow<User>

}