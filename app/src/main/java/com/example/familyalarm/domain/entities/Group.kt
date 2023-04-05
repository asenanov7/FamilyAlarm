package com.example.familyalarm.domain.entities

data class Group(
    val id: String? = null,
    val parent:UserParent? = null,
    val usersInGroup: List<UserChild?>? = null
)
