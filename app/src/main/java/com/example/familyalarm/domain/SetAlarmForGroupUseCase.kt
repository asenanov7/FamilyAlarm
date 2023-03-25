package com.example.familyalarm.domain

class SetAlarmForGroupUseCase(private val repository: Repository) {
    operator fun invoke(time:Int, userGroupId: String){
        repository.setAlarmForGroup(time, userGroupId)
    }
}