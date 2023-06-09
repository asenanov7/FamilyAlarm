package com.example.familyalarm.domain.usecases.alarm

import com.example.familyalarm.domain.entities.Alarm
import com.example.familyalarm.domain.repositories.AlarmRepository

class StopAlarmUseCase(private val repository: AlarmRepository) {
    operator fun invoke(parentId:String, alarm: Alarm){
        repository.stopAlarm(parentId, alarm)
    }
}