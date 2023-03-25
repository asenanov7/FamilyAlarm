package com.example.familyalarm.domain

import com.example.familyalarm.domain.entities.Alarm
import com.example.familyalarm.domain.entities.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface Repository {

    fun login(email:String, password:String): StateFlow<Any>

    fun register(email:String, password:String): StateFlow<Any>

    fun inviteUserInTheGroup(userId: String, userGroupId: String)

    fun deleteUserFromGroup(userId: String, userGroupId: String)

    fun setAlarmForGroup(time:Int, userGroupId: String)

    fun removeAlarmForGroup(time:Int, userGroupId: String)

    fun getUsersFromTheGroup(userGroupId: String): Flow<List<User>>

    fun getUserInfo(userId: String): Flow<User>

    fun startAlarm(userGroupId: String, alarm: Alarm)

    fun stopAlarm(userGroupId: String, alarm: Alarm)

}