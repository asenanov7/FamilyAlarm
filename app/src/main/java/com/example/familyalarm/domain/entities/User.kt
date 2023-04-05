package com.example.familyalarm.domain.entities


abstract class User(
    open val id: String? = null,
    open val name: String? = null,
    open val email: String? = null,
    open val password: String? = null,
)

data class UserChild(
    override val id: String? = null,
    override val name: String? = null,
    override val email: String? = null,
    override val password: String? = null,
    val awake: Boolean = false,
    val currentGroupId: String? = null,
):User()

data class UserParent(
    override val id: String? = null,
    override val name: String? = null,
    override val email: String? = null,
    override val password: String? = null,
    val personalGroupId: String? = null,
):User()
