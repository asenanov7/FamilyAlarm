package com.example.familyalarm.domain

data class User(
    val id: String,
    val name: String,
    val email: String,
    val awake: Boolean,
    val avatarUrl: String?,
    val groupLink: String?,
)