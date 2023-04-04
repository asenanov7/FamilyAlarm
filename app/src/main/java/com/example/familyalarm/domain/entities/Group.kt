package com.example.familyalarm.domain.entities

data class Group(
    val id: String? = null,
    val usersInGroup: List<User>? = null
)
