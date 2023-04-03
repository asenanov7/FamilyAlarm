package com.example.familyalarm.domain.entities

data class Group(
    val id: String,
    val users: MutableList<User>
){
    fun add(user:User){
        users.add(user)
    }
}