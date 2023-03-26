package com.example.familyalarm.data.impl_repositories

import com.example.familyalarm.data.Mapper
import com.example.familyalarm.domain.repositories.Repository
import com.example.familyalarm.domain.entities.Alarm
import com.example.familyalarm.domain.entities.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow

class RepositoryImpl(): Repository {

    private val mapper = Mapper()
    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun inviteUserInTheGroup(userId: String, userGroupId: String) {
        TODO("Not yet implemented")
    }

    override fun deleteUserFromGroup(userId: String, userGroupId: String) {
        TODO("Not yet implemented")
    }

    override fun setAlarmForGroup(time: Int, userGroupId: String) {
        TODO("Not yet implemented")
    }

    override fun removeAlarmForGroup(time: Int, userGroupId: String) {
        TODO("Not yet implemented")
    }

    override fun getUsersFromTheGroup(userGroupId: String): Flow<List<User>> {
        TODO("Not yet implemented")
    }

    override fun getUserInfo(userId: String): Flow<User> {
        TODO("Not yet implemented")
    }

    override fun startAlarm(userGroupId: String, alarm: Alarm) {
        TODO("Not yet implemented")
    }

    override fun stopAlarm(userGroupId: String, alarm: Alarm) {
        TODO("Not yet implemented")
    }




}