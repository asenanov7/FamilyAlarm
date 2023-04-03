package com.example.familyalarm.domain.entities

data class User(
    val id: String,
    val name: String,
    val email: String,
    val password: String,
    val awake: Boolean = false,
    val personalGroupId: String,
    val currentGroupId: String,
    val isLeader: Boolean = personalGroupId==currentGroupId
)