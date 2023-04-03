package com.example.familyalarm.domain.usecases.alarm

import com.example.familyalarm.domain.repositories.AlarmRepository
import com.example.familyalarm.domain.repositories.Repository

class RemoveAlarmForGroupUseCase(private val repository: AlarmRepository) {
    operator fun invoke(time:Int, userGroupId: String){
        repository.removeAlarmForGroup(time, userGroupId)
    }
}