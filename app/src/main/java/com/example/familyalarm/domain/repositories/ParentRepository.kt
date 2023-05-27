package com.example.familyalarm.domain.repositories

import com.example.familyalarm.domain.entities.User
import com.example.familyalarm.domain.entities.UserChild
import kotlinx.coroutines.flow.Flow

interface ParentRepository {

    suspend fun inviteChild(userId: String): Boolean

    suspend fun deleteChild(userId: String, parentId: String)

    suspend fun findChildByHazyName(name: String): Flow<List<UserChild>>

    val childsFLOW: Flow<List<UserChild>>


}