package com.example.familyalarm.domain.entities

data class User(
    val id: String,
    val name: String,
    val email: String,
    val awake: Boolean,
    val personalGroup: Group,
    val currentGroup: Group,
    val isLeader: Boolean = currentGroup==personalGroup
)