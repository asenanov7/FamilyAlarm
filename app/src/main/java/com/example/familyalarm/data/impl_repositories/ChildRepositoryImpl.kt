package com.example.familyalarm.data.impl_repositories

import android.util.Log
import com.example.familyalarm.data.impl_repositories.ChildAndParentUtils.Companion.getOldChildIdsList
import com.example.familyalarm.data.impl_repositories.ChildAndParentUtils.Companion.getUserChild
import com.example.familyalarm.data.impl_repositories.ChildAndParentUtils.Companion.removeInviteAfterAccept
import com.example.familyalarm.data.impl_repositories.ChildAndParentUtils.Companion.setNewChildList
import com.example.familyalarm.data.impl_repositories.ChildAndParentUtils.Companion.updateChildCurrentGroupId
import com.example.familyalarm.data.impl_repositories.GeneralRepositoryImpl.childsRef
import com.example.familyalarm.data.impl_repositories.GeneralRepositoryImpl.parentsRef
import com.example.familyalarm.data.listeners.SingleFirebaseListener
import com.example.familyalarm.domain.entities.UserChild
import com.example.familyalarm.domain.entities.UserParent
import com.example.familyalarm.domain.repositories.ChildRepository
import com.example.familyalarm.utils.FirebaseTables
import com.example.familyalarm.utils.FirebaseTables.PARENT_CHILDRENS_CHILD_TABLE
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

object ChildRepositoryImpl : ChildRepository {

    private val scope = CoroutineScope(Dispatchers.IO)
    private val parentRepositoryImpl = ParentRepositoryImpl

    private var needUpdateChild = MutableSharedFlow<Unit>()
    private var childs = emptyList<UserChild>()

    private val childsListener = object : SingleFirebaseListener<UserChild>() {

        override fun onDataChangeCustom(snapshot: DataSnapshot) {
            Log.d("ARSEN", "CHANGED $this")
            scope.launch {
                val listOfChilds = mutableListOf<UserChild>()
                for (item in snapshot.children) {
                    item.getValue<String>()
                        ?.let { getUserChild(it) }
                        ?.let {
                            if (it.id!=Firebase.auth.currentUser!!.uid) {
                                listOfChilds.add(it)
                            }
                        }
                }
                childs = listOfChilds
                needUpdateChild.emit(Unit)
            }
        }

        override fun onCanceledCustom(error: DatabaseError) {
            throw Exception("Canceled ChildOnParentListener in ParentRepositoryImpl")
        }
    }


    override suspend fun getInvitations(userId: String): Flow<List<UserParent>> {
        val invitations = MutableSharedFlow<List<UserParent>>(replay = 1)
        childsRef.child(userId).child(FirebaseTables.CHILD_INVITES_CHILD_TABLE)
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

    override suspend fun acceptInvite(parentId: String) {
        val childUserID = Firebase.auth.currentUser!!.uid
        val userChild = getUserChild(childUserID)

        scope.launch {
            if (userChild?.currentGroupId != null) {
                parentRepositoryImpl.deleteChild(
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

    override val getChilds: Flow<List<UserChild>> = flow {
        val child = getUserChild(Firebase.auth.currentUser!!.uid)
        val parentId = child?.currentGroupId

        if (!childsListener.isActive) {
            childsListener.attachListener(parentsRef, parentId!!, PARENT_CHILDRENS_CHILD_TABLE)
            Log.d("ARSEN", " childsListener.attachListener")
        }

        needUpdateChild.collect {
            emit(childs)
        }
    }
        .stateIn(scope = scope, started = SharingStarted.Lazily, emptyList())
}