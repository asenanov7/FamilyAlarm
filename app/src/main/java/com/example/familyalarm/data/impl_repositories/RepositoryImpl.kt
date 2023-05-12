package com.example.familyalarm.data.impl_repositories

import android.util.Log
import com.example.familyalarm.data.Mapper
import com.example.familyalarm.data.listeners.ChildOnParentListener
import com.example.familyalarm.domain.entities.User
import com.example.familyalarm.domain.entities.UserChild
import com.example.familyalarm.domain.entities.UserParent
import com.example.familyalarm.domain.repositories.Repository
import com.example.familyalarm.utils.FirebaseTables.CHILDS
import com.example.familyalarm.utils.FirebaseTables.GENERAL
import com.example.familyalarm.utils.FirebaseTables.INVITES
import com.example.familyalarm.utils.FirebaseTables.PARENTS
import com.example.familyalarm.utils.FirebaseTables.PARENT_CHILDRENS
import com.example.familyalarm.utils.databaseUrl
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
    private val childOnParentListener = ChildOnParentListener(this, scope)

    private val childsRef = Firebase.database(databaseUrl).getReference(CHILDS)
    private val parentsRef = Firebase.database(databaseUrl).getReference(PARENTS)
    private val generalRef = Firebase.database(databaseUrl).getReference(GENERAL)


    override suspend fun createChild(userChild: UserChild) {
        scope.launch {
            auth.currentUser?.let { childsRef.child(it.uid).setValue(userChild) }
        }.join()
    }

    override suspend fun createParent(userParent: UserParent) {
        scope.launch {
            auth.currentUser?.let { parentsRef.child(it.uid).setValue(userParent) }
        }.join()
    }


    override suspend fun getInvitations(userId: String): MutableSharedFlow<List<UserParent>> {
        val invitations = MutableSharedFlow<List<UserParent>>(replay = 1)
        childsRef.child(userId).child(INVITES)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    var parentsIds = listOf<String>()
                    scope.launch {
                        for (item in snapshot.children) {
                            val newListIds = parentsIds.toMutableList()
                            newListIds.add(item.getValue<String>()!!)
                            parentsIds = newListIds
                        }
                        val parents = parentsIds.map {
                                parentsRef.child(it).get().await().getValue<UserParent>()
                                    ?: throw Exception("Parent == null")
                            }
                        invitations.emit(parents)
                    }
                }
            })
        return invitations
    }

    override suspend fun inviteUser(userId: String): Boolean {
        val parentId = Firebase.auth.currentUser!!.uid
        return try {
            addNewInvites(userId, parentId)
            Log.d("inviteUser", "TRUE: updateInvites($userId, $parentId)")
            true
        } catch (e: java.lang.Exception) {
            Log.d("inviteUser", "FALSE: updateInvites($userId, $parentId)")
            false
        }
    }

    override suspend fun acceptInvite(parentId: String) {
        val childUserID = Firebase.auth.currentUser!!.uid
        val userChild = getUserChild(childUserID)

        scope.launch {
            if (userChild?.currentGroupId != null) {
                deleteChild(
                    userId = childUserID,
                    parentId = getUserChild(childUserID)?.currentGroupId ?:
                    throw Exception("Delete user From current parent error, currentParent == null"
                    )
                )
            }
        }.join()
        updateChildCurrentGroupId(childUserID, parentId)
            scope.launch {
                removeInviteAfterAccept(childUserID, parentId)

                val oldChildIdsList = getOldChildIdsList(parentId)
                val newList = oldChildIdsList.toMutableList()

                newList.add(childUserID)
                setNewChildList(parentId, newList)
            }
    }

    override suspend fun findUserByHazyName(name: String): Flow<List<UserChild>> = flow {
        Log.d("findUserByHazyName", "findUserByHazyName: $name")
        val childrensMap = childsRef.get().await().children
        val listContainsChild = mutableListOf<UserChild>()
        for (childItem in childrensMap) {
            val child = childItem.getValue(UserChild::class.java)
            if (child?.currentGroupId == Firebase.auth.currentUser!!.uid) {
                Log.d("findUserByHazyName", "findUserByHazyName: continue")
                continue
            }
            if (child?.name?.lowercase()?.contains(name.lowercase()) == true) {
                Log.d("findUserByHazyName", "findUserByHazyName: $child")
                listContainsChild.add(child)
            }
        }
        emit(listContainsChild)
    }

    override fun deleteChild(userId: String, parentId: String) {
        scope.launch {
            val oldIdList = getOldChildIdsList(parentId)
            val newIdList = oldIdList.toMutableList()
                .apply { remove(userId) }

            setNewChildList(parentId, newIdList)
            updateChildCurrentGroupId(userId, null)
        }
    }

    override suspend fun getUserInfo(userId: String): User {
        return try {
            childsRef.child(userId).get().await().getValue<UserChild>()!!
        } catch (e: java.lang.Exception) {
            parentsRef.child(userId).get().await().getValue<UserParent>()!!
        }
    }


    suspend fun getUserChild(userId: String): UserChild? {
        val snapshot = childsRef.child(userId).get().await()
        return snapshot.getValue(UserChild::class.java)
    }


    private suspend fun getOldChildIdsList(parentId: String): List<String?> {
        val snapshot = parentsRef.child(parentId).child(PARENT_CHILDRENS).get().await()
        return snapshot.getValue<List<String>>() ?: listOf()
    }


    private suspend fun setNewChildList(parentId: String, newIdList: List<String?>) {
        parentsRef.child(parentId).child(PARENT_CHILDRENS ).setValue(newIdList).await()
    }

    private suspend fun updateChildCurrentGroupId(userChildId: String, groupId: String?){
       childsRef.child(userChildId).get().await().getValue(UserChild::class.java)?.let {
                if (it.currentGroupId == groupId) {
                    throw Exception("У пользователя oldгруппа == новой присваеваемой")
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
                    generalRef.child(userChild?.id ?: "null id").setValue(userChild)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("setGeneralAutoChange childs", "onCancelled: $error ")
            }
        })

        parentsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (item in snapshot.children) {
                    val userParent = item.getValue(UserParent::class.java)
                    generalRef.child(userParent?.id ?: "null id").setValue(userParent)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("setGeneralAutoChange parents", "onCancelled: $error ")
            }
        })
    }


    override fun getChilds(parentId: String): Flow<List<UserChild>> {
        if (!childOnParentListener.isActive) {
            childOnParentListener.addListener(parentId)
        }
        return childOnParentListener.result
    }

}


