package com.example.familyalarm.domain.entities

data class Alarm(
    val parent: UserParent,
    val group: Group,
    val vibration: Boolean,
    val duration: Int,
)