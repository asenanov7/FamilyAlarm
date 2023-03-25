package com.example.familyalarm.domain.entities

data class UserGroup(
    val id: String,
    val bossId:String,
    val users: List<User>
)