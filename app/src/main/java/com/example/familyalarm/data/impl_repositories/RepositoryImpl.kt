package com.example.familyalarm.data.impl_repositories

import android.util.Log
import com.example.familyalarm.data.Mapper
import com.example.familyalarm.domain.repositories.Repository
import com.example.familyalarm.domain.entities.Alarm
import com.example.familyalarm.domain.entities.Group
import com.example.familyalarm.domain.entities.User
import com.example.familyalarm.utils.FirebaseTables
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class RepositoryImpl() : Repository {

    private val mapper = Mapper()
    private val auth by lazy { FirebaseAuth.getInstance() }

    private val groupsRef = Firebase.firestore.collection(FirebaseTables.GROUPS)
    private val usersRef = Firebase.firestore.collection(FirebaseTables.USERS)


    override fun createUserUseCase(user: User) {
        auth.currentUser?.let {
            val group = Group(it.uid, mutableListOf())
            group.add(user)

            val groupHashMap = hashMapOf(
                "id" to group.id,
                "users" to group.users
            )

            groupsRef
                .document(it.uid)
                .set(groupHashMap)
                .addOnSuccessListener {
                    auth.currentUser?.let {
                        Log.d("FIRESTORE", "groupHashMap: Success")
                        val userHashmap = hashMapOf(
                            "id" to it.uid,
                            "name" to user.name,
                            "email" to user.email,
                            "password" to user.password,
                            "awake" to user.awake,
                            "personalGroup" to user.personalGroupId,
                            "currentGroup" to user.currentGroupId,
                            "isLeader" to user.isLeader
                        )

                        usersRef
                            .document(it.uid)
                            .set(userHashmap)
                            .addOnSuccessListener { Log.d("FIRESTORE", "userHashmap: Success") }
                            .addOnFailureListener { Log.d("FIRESTORE", "userHashmap: Fail") }
                    }
                }
                .addOnFailureListener { Log.d("FIRESTORE", "groupHashMap: Fail") }

        }
    }

    override fun inviteUserInTheGroup(userId: String, userGroupId: String) {
        TODO("Not yet implemented")
    }

    override fun deleteUserFromGroup(userId: String, userGroupId: String) {
        TODO("Not yet implemented")
    }

    override fun getUsersFromTheGroup(userGroupId: String): Flow<List<User>> {
        TODO("Not yet implemented")
    }

    override fun getUserInfo(userId: String): Flow<User> {
        TODO("Not yet implemented")
    }




}