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
import kotlin.math.log

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

    private val generalRef =
        Firebase.database(databaseUrl)
            .getReference(FirebaseTables.GENERAL)


    override suspend fun createChildUseCase(userChild: UserChild) {
        Log.d(TAG, "createChildUseCase: started")
        val job = scope.launch {
            auth.currentUser?.let {
                Log.d(TAG, "createChildUseCase: currentUser!=null")
                childsRef
                    .child(it.uid)
                    .setValue(userChild)
                    .addOnSuccessListener {
                        Log.d(TAG, "createChildUseCase: user Success")
                    }
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
                        Log.d(TAG, "createParentUseCase: user Success")
                    }
                    .addOnFailureListener { Log.d(TAG, "createParentUseCase: user Fail") }
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
            addNewInvites(userId, parentId)
            Log.d(TAG, "TRUE: updateInvites($userId, $parentId)")
            true
        } catch (e: java.lang.Exception) {
            Log.d(TAG, "FALSE: updateInvites($userId, $parentId)")
            false
        }
    }

    override suspend fun acceptInvite(parentId: String) {
        val childUserID = Firebase.auth.currentUser!!.uid
        updateChildCurrentGroupId(childUserID, parentId)?.addOnSuccessListener {
            scope.launch {
                removeInviteAfterAccept(childUserID, parentId)
                val userChild = getUserChild(childUserID)
                val oldChildList = getOldChildList(parentId)

                if (userChild != null) {
                    val newList = oldChildList.toMutableList()
                    newList.add(userChild)
                    setNewList(parentId, newList)
                }
            }
        }
    }

    override suspend fun findUserByHazyName(name: String): Flow<List<UserChild>> = flow {
        Log.d(TAG, "findUserByHazyName: $name")
        val childrensMap = childsRef.get().await().children
        val listContainsChild = mutableListOf<UserChild>()
        for (childItem in childrensMap) {
            val child = childItem.getValue(UserChild::class.java)
            if (child?.currentGroupId == Firebase.auth.currentUser!!.uid) {
                Log.d(TAG, "findUserByHazyName: continue")
                continue
            }
            if (child?.name?.lowercase()?.contains(name) == true) {
                Log.d(TAG, "findUserByHazyName: $child")
                listContainsChild.add(child)
            }
        }
        emit(listContainsChild)
    }

    override fun deleteUserFromParentChildrens(userId: String, parentId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getUserInfo(userId: String): User {
        return try {
            childsRef.child(userId).get().await().getValue<UserChild>()!!
        }catch (e:java.lang.Exception) {
            parentsRef.child(userId).get().await().getValue<UserParent>()!!
        }
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

    private suspend fun addNewInvites(userChildId: String, parentId: String) {
        childsRef.child(userChildId).get().await().getValue(UserChild::class.java)?.let {
            val oldInvites = it.invitesParentsID ?: listOf()
            val newInvites = oldInvites.toMutableList()
            if (!newInvites.contains(parentId)) {
                newInvites.add(parentId)
            }

            childsRef.child(userChildId).setValue(it.copy(invitesParentsID = newInvites))
        }
    }

    private suspend fun removeInviteAfterAccept(userChildId: String, parentId: String) {
        childsRef.child(userChildId).get().await().getValue(UserChild::class.java)?.let {
            val oldInvites = it.invitesParentsID ?: listOf()
            Log.d("removeInviteAfterAccept", "oldInvites: $oldInvites ")
            val newInvites = oldInvites.toMutableList()
            if (newInvites.contains(parentId)) {
                Log.d(TAG, "newInvites remove: ")
                newInvites.remove(parentId)
                Log.d("removeInviteAfterAccept", "newInvites($newInvites) remove: $parentId ")
            } else {
                throw Exception("removeInviteAfterAccept newInvites not contains parentId for remove")
            }
            childsRef.child(userChildId).setValue(it.copy(invitesParentsID = newInvites))
        }
    }

    fun setGeneralAutoChange() {
        childsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (item in snapshot.children) {
                    val userChild = item.getValue(UserChild::class.java)
                    generalRef
                        .child(userChild?.id ?: "null id")
                        .setValue(userChild)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        parentsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (item in snapshot.children) {
                    val userParent = item.getValue(UserParent::class.java)
                    generalRef
                        .child(userParent?.id ?: "null id")
                        .setValue(userParent)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}

