package com.example.familyalarm.domain.repositories

import com.example.familyalarm.domain.entities.User
import kotlinx.coroutines.flow.Flow

interface Repository {

    fun createUserUseCase(user:User)

    fun inviteUserInTheGroup(userId: String, userGroupId: String)

    fun deleteUserFromGroup(userId: String, userGroupId: String)

    fun getUsersFromTheGroup(userGroupId: String): Flow<List<User>>

    fun getUserInfo(userId: String): Flow<User>

}