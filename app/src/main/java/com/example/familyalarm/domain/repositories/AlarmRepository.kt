package com.example.familyalarm.domain.repositories

import com.example.familyalarm.domain.entities.Alarm

interface AlarmRepository {

    fun setAlarmForParentChildrens(time:Int, parentId: String)

    fun removeAlarmForParentChildrens(time:Int, parentId: String)

    fun startAlarm(parentId: String, alarm: Alarm)

    fun stopAlarm(parentId: String, alarm: Alarm)
}