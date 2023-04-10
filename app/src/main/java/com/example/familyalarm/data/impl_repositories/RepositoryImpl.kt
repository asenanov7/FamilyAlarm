package com.example.familyalarm.data.impl_repositories

import android.util.Log
import com.example.familyalarm.data.Mapper
import com.example.familyalarm.domain.entities.User
import com.example.familyalarm.domain.entities.UserChild
import com.example.familyalarm.domain.entities.UserParent
import com.example.familyalarm.domain.repositories.Repository
import com.example.familyalarm.utils.FirebaseTables
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RepositoryImpl() : Repository {

    private val mapper = Mapper()
    private val auth by lazy { FirebaseAuth.getInstance() }
    private val scope = CoroutineScope(Dispatchers.IO)

    companion object {
        const val TAG = "RepositoryImpl"
        const val databaseUrl =
            "https://waketeam-75be8-default-rtdb.europe-west1.firebasedatabase.app"
    }

    private val childsRef =
        Firebase.database(databaseUrl)
            .getReference(FirebaseTables.CHILDS)

    private val parentsRef =
        Firebase.database(databaseUrl)
            .getReference(FirebaseTables.PARENTS)


    override suspend fun createChildUseCase(userChild: UserChild) {
        Log.d(TAG, "createChildUseCase: started")
        val job = scope.launch {
            auth.currentUser?.let {
                Log.d(TAG, "createChildUseCase: currentUser!=null")
                childsRef
                    .child(it.uid)
                    .setValue(userChild)
                    .addOnSuccessListener { Log.d(TAG, "createChildUseCase: user Success") }
                    .addOnFailureListener { Log.d(TAG, "createChildUseCase: user Fail") }
            }
        }
        job.join()
    }

    override suspend fun createParentUseCase(userParent: UserParent) {
        Log.d(TAG, "createParentUseCase: started")
        auth.currentUser?.let { firebaseUser ->
            Log.d(TAG, "createParentUseCase: currentUser!=null")

            val job = scope.launch() {
                parentsRef
                    .child(firebaseUser.uid)
                    .setValue(userParent)
                    .addOnSuccessListener {
                        Log.d(
                            TAG,
                            "createParentUseCase: user Success"
                        )
                    }
                    .addOnFailureListener {
                        Log.d(
                            TAG,
                            "createParentUseCase: user Fail"
                        )
                    }
            }
            job.join()
        }
    }

    override fun getUsersFromParentChildrens(parentId: String): MutableSharedFlow<List<UserChild>> {

        val listUserMutableFlow = MutableSharedFlow<List<UserChild>>(replay = 1)

        parentsRef.child(parentId).child("childrens")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var listUser = listOf<UserChild>()
                    Log.d(TAG, "onDataChange $snapshot")
                    for (item in snapshot.children) {
                        Log.d(TAG, "(item in snapshot.children ")
                        val user = item.getValue(UserChild::class.java)
                        if (user != null) {
                            Log.d(TAG, "user != null ")
                            val newList = listUser.toMutableList()
                            newList.add(user)
                            listUser = newList
                        }
                        Log.d(TAG, "listResult: $listUser")
                        scope.launch {
                            listUserMutableFlow.emit(listUser)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "onCancelled $error ")
                    throw Exception(error.message)
                }
            }
            )
        return listUserMutableFlow
    }

    override suspend fun inviteUserInTheParentChildrens(userId: String): Boolean {
        val parentId = Firebase.auth.currentUser!!.uid
        return try {
        updateChildCurrentGroupId(userId, parentId)?.addOnSuccessListener {
                scope.launch {
                    val userChild = getUserChild(userId)
                    val oldChildList = getOldChildList(parentId)

                    if (userChild != null) {
                        val newList = oldChildList.toMutableList()
                        newList.add(userChild)
                        setNewList(parentId, newList)
                    }

                }
            }
            true
        }catch (e: java.lang.Exception) {
            false
        }
    }

    override suspend fun findUserByHazyName(name: String): Flow<List<UserChild>> = flow {
        val childrensMap = childsRef.get().await().children
        val listContainsChild = mutableListOf<UserChild>()
        for (childItem in childrensMap) {
            val child = childItem.getValue(UserChild::class.java)
            if (child?.name?.lowercase()?.contains(name) == true) {
                listContainsChild.add(child)
            }
        }
        emit(listContainsChild)
    }

    override fun deleteUserFromParentChildrens(userId: String, parentId: String) {
        TODO("Not yet implemented")
    }

    override fun getUserInfo(userId: String): Flow<User> {
        TODO("Not yet implemented")
    }


    private suspend fun getUserChild(userId: String): UserChild? {
        val snapshot = childsRef.child(userId).get().await()
        return snapshot.getValue(UserChild::class.java)
    }

    private suspend fun getOldChildList(parentId: String): List<UserChild> {
        val snapshot = parentsRef.child(parentId).child("childrens").get().await()
        return snapshot.getValue<List<UserChild>>() ?: listOf()
    }

    private suspend fun setNewList(parentId: String, newList: List<UserChild>) {
        parentsRef.child(parentId).child("childrens").setValue(newList).await()
    }

    private suspend fun updateChildCurrentGroupId(
        userChildId: String,
        groupId: String
    ): Task<Void>? {
        return childsRef.child(userChildId).get().await().getValue(UserChild::class.java)
            ?.let {
                if (it.currentGroupId == groupId) {
                    throw Exception("Пользователь уже находится в группе")
                }
                childsRef.child(userChildId).setValue(it.copy(currentGroupId = groupId))
            }
    }

}