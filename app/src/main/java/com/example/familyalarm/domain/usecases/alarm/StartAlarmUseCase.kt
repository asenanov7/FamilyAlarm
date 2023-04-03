package com.example.familyalarm.domain.usecases.alarm

import com.example.familyalarm.domain.entities.Alarm
import com.example.familyalarm.domain.repositories.AlarmRepository
import com.example.familyalarm.domain.repositories.Repository

class StartAlarmUseCase(private val repository: AlarmRepository) {
    operator fun invoke(userGroupId:String, alarm: Alarm){
        repository.startAlarm(userGroupId, alarm)
    }
}