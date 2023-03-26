package com.example.familyalarm.domain.usecases

import com.example.familyalarm.domain.repositories.Repository

class RemoveAlarmForGroupUseCase(private val repository: Repository) {
    operator fun invoke(time:Int, userGroupId: String){
        repository.removeAlarmForGroup(time, userGroupId)
    }
}