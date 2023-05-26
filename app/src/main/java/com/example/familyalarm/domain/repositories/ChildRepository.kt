package com.example.familyalarm.domain.repositories

import com.example.familyalarm.domain.entities.UserChild
import com.example.familyalarm.domain.entities.UserParent
import kotlinx.coroutines.flow.Flow

interface ChildRepository {

    suspend fun getInvitations(userId: String): Flow<List<UserParent>>

    suspend fun acceptInvite(parentId: String)


    val getChilds: Flow<List<UserChild>>

}