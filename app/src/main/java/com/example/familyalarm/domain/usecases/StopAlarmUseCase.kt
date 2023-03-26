package com.example.familyalarm.domain.usecases

import com.example.familyalarm.domain.entities.Alarm
import com.example.familyalarm.domain.repositories.Repository

class StopAlarmUseCase(private val repository: Repository) {
    operator fun invoke(userGroupId:String, alarm: Alarm){
        repository.stopAlarm(userGroupId, alarm)
    }
}