package com.example.familyalarm.domain.usecases

import com.example.familyalarm.domain.entities.Alarm
import com.example.familyalarm.domain.Repository

class StartAlarmUseCase(private val repository: Repository) {
    operator fun invoke(userGroupId:String, alarm: Alarm){
        repository.startAlarm(userGroupId, alarm)
    }
}