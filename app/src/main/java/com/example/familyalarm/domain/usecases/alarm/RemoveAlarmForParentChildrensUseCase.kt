package com.example.familyalarm.domain.usecases.alarm

import com.example.familyalarm.domain.repositories.AlarmRepository

class RemoveAlarmForParentChildrensUseCase(private val repository: AlarmRepository) {
    operator fun invoke(time:Int, userGroupId: String){
        repository.removeAlarmForParentChildrens(time, userGroupId)
    }
}