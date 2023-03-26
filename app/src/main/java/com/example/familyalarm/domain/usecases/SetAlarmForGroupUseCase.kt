package com.example.familyalarm.domain.usecases

import com.example.familyalarm.domain.repositories.Repository

class SetAlarmForGroupUseCase(private val repository: Repository) {
    operator fun invoke(time:Int, userGroupId: String){
        repository.setAlarmForGroup(time, userGroupId)
    }
}