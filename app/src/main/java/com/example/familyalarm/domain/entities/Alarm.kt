package com.example.familyalarm.domain.entities

data class Alarm(
    val hostUser: User,
    val group: Group,
    val vibration: Boolean,
    val duration: Int,
)