package com.example.familyalarm.domain.repositories

import com.example.familyalarm.domain.entities.User
import com.example.familyalarm.domain.entities.UserChild
import com.example.familyalarm.domain.entities.UserParent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

interface Repository {

    suspend fun createChild(userChild: UserChild)

    suspend fun createParent(userParent: UserParent)

    suspend fun inviteUser(userId: String): Boolean

    suspend fun getInvitations(userId: String): MutableSharedFlow<List<UserParent>>

    suspend fun acceptInvite(parentId: String)

    suspend fun findUserByHazyName(name:String): Flow<List<UserChild>>

    fun deleteChild(userId: String, parentId: String)

    fun getChilds(parentId: String): Flow<List<UserChild>>

    suspend fun getUserInfo(userId: String): User

}