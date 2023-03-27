package com.example.familyalarm.domain.entities

data class Group(
    val id: String,
    val bossId:String,
    val users: List<User>
)