package com.example.familyalarm.data.impl_repositories

import android.util.Log
import com.example.familyalarm.data.impl_repositories.ChildAndParentUtils.Companion.getOldChildIdsList
import com.example.familyalarm.data.impl_repositories.ChildAndParentUtils.Companion.getUserChild
import com.example.familyalarm.data.impl_repositories.ChildAndParentUtils.Companion.removeInviteAfterAccept
import com.example.familyalarm.data.impl_repositories.ChildAndParentUtils.Companion.setNewChildList
import com.example.familyalarm.domain.entities.UserChild
import com.example.familyalarm.domain.entities.UserParent
import com.example.familyalarm.domain.repositories.ChildRepository
import com.example.familyalarm.utils.FirebaseTables
import com.example.familyalarm.utils.FirebaseTables.Companion.CHILDS_REF
import com.example.familyalarm.utils.FirebaseTables.Companion.PARENTS_REF
import com.example.familyalarm.utils.FirebaseTables.Companion.PARENT_CHILDRENS_CHILD_TABLE
import com.example.familyalarm.utils.throwEx
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

class ChildRepositoryImpl private constructor() : ChildRepository {

    private val scope = CoroutineScope(Dispatchers.IO)

    private val childId = Firebase.auth.currentUser!!.uid

    private var needUpdateChilds = MutableSharedFlow<Unit>()
    private var currentGroupChanged = MutableSharedFlow<Unit>()

    private var childs = emptyList<UserChild>()

    private var currentGroupId: String? = null
    private var oldGroupId: String? = null





    private val childsListener = object : ValueEventListener {

        override fun onDataChange(snapshot: DataSnapshot) {
            Log.d("FIX_LISTENERS_BUG", "CHANGED on ${this@ChildRepositoryImpl} listener = $this")
            scope.launch {
                val listOfChilds = mutableListOf<UserChild>()
                for (item in snapshot.children) {
                    item.getValue<String>()?.let { getUserChild(it) }?.let {
                        if (it.id != Firebase.auth.currentUser!!.uid) {
                            listOfChilds.add(it)
                        }
                    }
                }
                childs = listOfChilds
                needUpdateChilds.emit(Unit)
            }
        }

        override fun onCancelled(error: DatabaseError) {
            throw Exception("Canceled ChildOnParentListener in ParentRepositoryImpl")
        }
    }

    private val currentGroupIdListener = object : ValueEventListener {

        override fun onDataChange(snapshot: DataSnapshot) {
            Log.d("FIX_LISTENERS_BUG", "CHANGED on ${this@ChildRepositoryImpl} listener = $this")
            oldGroupId = currentGroupId
            currentGroupId = snapshot.getValue<String>()
            scope.launch { currentGroupChanged.emit(Unit) }
        }

        override fun onCancelled(error: DatabaseError) {
            throwEx(onCancelled(error))
        }
    }


    override val getChilds: Flow<List<UserChild>> = flow {
        scope.launch {
            oldGroupId = getUserChild(Firebase.auth.currentUser!!.uid)!!.currentGroupId
            currentGroupId = getUserChild(Firebase.auth.currentUser!!.uid)!!.currentGroupId
        }.join()

        val ref = CHILDS_REF.child(childId).child("currentGroupId")
        ref.addValueEventListener(currentGroupIdListener)

        scope.launch {
            currentGroupChanged.collectLatest{
                    Log.d("FIX_LISTENERS", "current group changed")
                    scope.launch {
                        val oldRef = PARENTS_REF
                            .child(oldGroupId ?: "")
                            .child(PARENT_CHILDRENS_CHILD_TABLE)

                        val newRef = PARENTS_REF
                            .child(currentGroupId ?: "")
                            .child(PARENT_CHILDRENS_CHILD_TABLE)

                        oldRef.removeEventListener(childsListener)
                        newRef.addValueEventListener(childsListener)
                    }.join()
                    needUpdateChilds.emit(Unit)
                }
        }

        needUpdateChilds.collect {
            emit(childs)
        }

    }.stateIn(scope = scope, started = SharingStarted.Lazily, emptyList())


    override suspend fun getInvitations(userId: String): Flow<List<UserParent>> {
        val invitations = MutableSharedFlow<List<UserParent>>(replay = 1)
        CHILDS_REF.child(userId).child(FirebaseTables.CHILD_INVITES_CHILD_TABLE)
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
                            PARENTS_REF.child(it).get().await().getValue<UserParent>()
                                ?: throw Exception("Parent == null")
                        }
                        invitations.emit(parents)
                    }
                }
            })
        return invitations
    }

    override suspend fun acceptInvite(parentId: String) {
        Log.d("FIX_LISTENERS", "acceptInvite")

        val childUserID = Firebase.auth.currentUser!!.uid
        val userChild = getUserChild(childUserID)

        //delete user From oldParent
        scope.launch {
            if (userChild?.currentGroupId != null) {
                scope.launch {
                    val oldIdList = getOldChildIdsList(userChild.currentGroupId)
                    val newIdList = oldIdList.toMutableList().apply { remove(childUserID) }

                    setNewChildList(userChild.currentGroupId, newIdList)

                }.join()
            }
        }.join()

        //update currentParentId
        //Попробую убрать CHILDS_REF.child(childUserID).child("currentGroupId").setValue(parentId)
        ChildAndParentUtils.updateChildCurrentGroupId(childUserID, parentId)

        //add user in newParent
        scope.launch {
            removeInviteAfterAccept(childUserID, parentId)

            val oldChildIdsList = getOldChildIdsList(parentId)
            val newList = oldChildIdsList.toMutableList()

            newList.add(childUserID)
            setNewChildList(parentId, newList)
        }
    }

    companion object {

        private var INSTANCE: ChildRepositoryImpl? = null
        private val LOCK = Any()

        fun create(): ChildRepositoryImpl {
            INSTANCE?.let { return it }

            synchronized(LOCK) {
                INSTANCE?.let { return it }
                val childRepImpl = ChildRepositoryImpl()
                INSTANCE = childRepImpl
                return childRepImpl
            }
        }

        fun destroy() {
            PARENTS_REF
                .child(INSTANCE?.currentGroupId ?: "")
                .child(PARENT_CHILDRENS_CHILD_TABLE)
                .removeEventListener(INSTANCE?.childsListener!!)

            CHILDS_REF
                .child(INSTANCE?.childId?:"")
                .child("currentGroupId")
                .removeEventListener(INSTANCE?.currentGroupIdListener!!)

            Log.d("FIX_LISTENERS_BUG", "destroy: ${
                PARENTS_REF.child(INSTANCE?.currentGroupId ?: "").child(PARENT_CHILDRENS_CHILD_TABLE)}")

            Log.d("FIX_LISTENERS_BUG", "destroy: ${
            CHILDS_REF.child(INSTANCE?.childId?:"").child("currentGroupId")}")



            INSTANCE = null
        }


    }


}