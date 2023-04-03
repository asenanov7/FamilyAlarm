package com.example.familyalarm.domain.repositories

import com.example.familyalarm.domain.entities.Alarm

interface AlarmRepository {

    fun setAlarmForGroup(time:Int, userGroupId: String)

    fun removeAlarmForGroup(time:Int, userGroupId: String)

    fun startAlarm(userGroupId: String, alarm: Alarm)

    fun stopAlarm(userGroupId: String, alarm: Alarm)
}