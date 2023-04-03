package com.example.familyalarm.domain.usecases.alarm

import com.example.familyalarm.domain.repositories.AlarmRepository
import com.example.familyalarm.domain.repositories.Repository

class SetAlarmForGroupUseCase(private val repository: AlarmRepository) {
    operator fun invoke(time:Int, userGroupId: String){
        repository.setAlarmForGroup(time, userGroupId)
    }
}