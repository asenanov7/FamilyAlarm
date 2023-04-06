package com.example.familyalarm.domain.entities

data class Alarm(
    val parent: UserParent,
    val vibration: Boolean,
    val duration: Int,
)