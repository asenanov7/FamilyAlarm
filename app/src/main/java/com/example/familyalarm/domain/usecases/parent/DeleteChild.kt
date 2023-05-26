package com.example.familyalarm.domain.usecases.parent

import com.example.familyalarm.domain.repositories.ParentRepository

class DeleteChild(private val parentRepository: ParentRepository) {
    operator fun invoke(userId: String, parentId: String){
        parentRepository.deleteChild(userId, parentId)
    }
}