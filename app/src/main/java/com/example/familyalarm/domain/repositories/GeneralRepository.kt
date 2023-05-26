package com.example.familyalarm.domain.repositories

import com.example.familyalarm.domain.entities.UserChild
import com.example.familyalarm.domain.entities.UserParent

interface GeneralRepository {

    suspend fun createChild(userChild: UserChild)

    suspend fun createParent(userParent: UserParent)
    
    

}