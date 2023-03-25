package com.example.familyalarm.domain

class RemoveAlarmForGroupUseCase(private val repository: Repository) {
    operator fun invoke(time:Int, userGroupId: String){
        repository.removeAlarmForGroup(time, userGroupId)
    }
}