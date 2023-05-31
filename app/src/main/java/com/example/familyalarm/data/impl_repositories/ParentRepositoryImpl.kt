package com.example.familyalarm.data.impl_repositories

import android.util.Log
import com.example.familyalarm.data.impl_repositories.ChildAndParentUtils.Companion.addNewInvites
import com.example.familyalarm.data.impl_repositories.ChildAndParentUtils.Companion.getOldChildIdsList
import com.example.familyalarm.data.impl_repositories.ChildAndParentUtils.Companion.getUserChild
import com.example.familyalarm.data.impl_repositories.ChildAndParentUtils.Companion.setNewChildList
import com.example.familyalarm.data.impl_repositories.ChildAndParentUtils.Companion.updateChildCurrentGroupId
import com.example.familyalarm.domain.entities.UserChild
import com.example.familyalarm.domain.repositories.ParentRepository
import com.example.familyalarm.utils.FirebaseTables.Companion.CHILDS_REF
import com.example.familyalarm.utils.FirebaseTables.Companion.PARENTS_REF
import com.example.familyalarm.utils.FirebaseTables.Companion.PARENT_CHILDRENS_CHILD_TABLE
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

class ParentRepositoryImpl private constructor() : ParentRepository {

    private val scope = CoroutineScope(Dispatchers.IO)
    private var needUpdateChild = MutableSharedFlow<Unit>()
    private var childs = emptyList<UserChild>()
    private val parentId = Firebase.auth.uid!!

    private val childsListener = object : ValueEventListener {

        override fun onDataChange(snapshot: DataSnapshot) {
            Log.d("FIX_LISTENERS_BUG", "CHANGED on ${this@ParentRepositoryImpl} listener = $this")
            scope.launch {
                val listOfChilds = mutableListOf<UserChild>()
                for (item in snapshot.children) {
                    item.getValue<String>()
                        ?.let { getUserChild(it) }
                        ?.let {
                            listOfChilds.add(it)
                        }
                }
                childs = listOfChilds
                needUpdateChild.emit(Unit)
            }
        }

        override fun onCancelled(error: DatabaseError) {
            throw Exception("Canceled ChildOnParentListener in ParentRepositoryImpl")
        }
    }


    override val childsFLOW: Flow<List<UserChild>> = flow {

        PARENTS_REF.child(parentId).child(PARENT_CHILDRENS_CHILD_TABLE)
            .addValueEventListener(childsListener)

        needUpdateChild.collect {
            emit(childs)
        }

    }.stateIn(scope = scope, started = SharingStarted.Lazily, emptyList())

    override suspend fun inviteChild(userId: String): Boolean {
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


    override suspend fun deleteChild(userId: String, parentId: String) {
        scope.launch {
            val oldIdList = getOldChildIdsList(parentId)
            val newIdList = oldIdList.toMutableList().apply { remove(userId) }

            setNewChildList(parentId, newIdList)
            updateChildCurrentGroupId(userId, null)
        }.join()
    }


    override suspend fun findChildByHazyName(name: String): Flow<List<UserChild>> = flow {

        val childrensMap = CHILDS_REF.get().await().children
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

    companion object {

        private var INSTANCE: ParentRepositoryImpl? = null
        private val LOCK = Any()

        fun create(): ParentRepositoryImpl {
            INSTANCE?.let { return it }

            synchronized(LOCK) {
                INSTANCE?.let { return it }
                val parentRepImpl = ParentRepositoryImpl()
                INSTANCE = parentRepImpl
                return parentRepImpl
            }
        }

        fun destroy() {
            Log.d("FIX_LISTENERS_BUG", "destroy: ${ 
                PARENTS_REF.child(INSTANCE?.parentId?:"").child(PARENT_CHILDRENS_CHILD_TABLE)}"
            )
            PARENTS_REF.child(INSTANCE?.parentId?:"").child(PARENT_CHILDRENS_CHILD_TABLE)
                .removeEventListener(INSTANCE?.childsListener!!)


            INSTANCE = null
        }
    }

}
