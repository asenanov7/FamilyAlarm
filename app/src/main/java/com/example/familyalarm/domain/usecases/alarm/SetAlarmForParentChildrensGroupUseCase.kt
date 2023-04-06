package com.example.familyalarm.domain.usecases.alarm

import com.example.familyalarm.domain.repositories.AlarmRepository

class SetAlarmForParentChildrensGroupUseCase(private val repository: AlarmRepository) {
    operator fun invoke(time:Int, parentId: String){
        repository.setAlarmForParentChildrens(time, parentId)
    }
}