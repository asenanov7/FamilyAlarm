package com.example.familyalarm.domain

data class UserGroup(
    val id: String,
    val bossId:String,
    val users: List<User>
)