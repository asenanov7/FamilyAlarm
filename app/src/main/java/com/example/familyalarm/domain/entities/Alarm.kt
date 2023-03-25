package com.example.familyalarm.domain.entities

data class Alarm(
    val hostUser: User,
    val userGroup: UserGroup,
    val vibration: Boolean,
    val duration: Int,
)