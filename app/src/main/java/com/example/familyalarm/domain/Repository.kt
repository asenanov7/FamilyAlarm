package com.example.familyalarm.domain

import kotlinx.coroutines.flow.Flow

interface Repository {

    fun inviteUserInTheGroup(userId: String, userGroupId: String)

    fun deleteUserFromGroup(userId: String, userGroupId: String)

    fun setAlarmForGroup(time:Int, userGroupId: String)

    fun removeAlarmForGroup(time:Int, userGroupId: String)

    fun getUsersFromTheGroup(userGroupId: String): Flow<List<User>>

    fun getUserInfo(userId: String): Flow<User>

}