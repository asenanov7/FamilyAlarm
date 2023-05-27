package com.example.familyalarm.data.impl_repositories

import android.util.Log
import com.example.familyalarm.domain.entities.UserChild
import com.example.familyalarm.utils.FirebaseTables
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.tasks.await

class ChildAndParentUtils {

    companion object {

        suspend fun addNewInvites(userChildId: String, parentId: String) {
            GeneralRepositoryImpl.childsRef.child(userChildId).get().await().getValue(UserChild::class.java)?.let {
                val oldInvites = it.invitesParentsID ?: listOf()
                val newInvites = oldInvites.toMutableList()
                if (!newInvites.contains(parentId)) {
                    newInvites.add(parentId)
                }
                GeneralRepositoryImpl.childsRef.child(userChildId).setValue(it.copy(invitesParentsID = newInvites))
            }
        }

        suspend fun getUserChild(userId: String): UserChild? {
            val snapshot = GeneralRepositoryImpl.childsRef.child(userId).get().await()
            return snapshot.getValue(UserChild::class.java)
        }

        suspend fun removeInviteAfterAccept(userChildId: String, parentId: String) {
            GeneralRepositoryImpl.childsRef.child(userChildId).get().await().getValue(UserChild::class.java)?.let {
                val oldInvites = it.invitesParentsID ?: listOf()
                Log.d("removeInviteAfterAccept", "oldInvites: $oldInvites ")
                val newInvites = oldInvites.toMutableList()
                if (newInvites.contains(parentId)) {
                    newInvites.remove(parentId)
                    Log.d("removeInviteAfterAccept", "newInvites($newInvites) remove: $parentId ")
                } else {
                    throw Exception("removeInviteAfterAccept newInvites not contains parentId for remove")
                }
                GeneralRepositoryImpl.childsRef.child(userChildId).setValue(it.copy(invitesParentsID = newInvites))
            }
        }

        suspend fun setNewChildList(parentId: String, newIdList: List<String?>) {
            GeneralRepositoryImpl.parentsRef.child(parentId).child(FirebaseTables.PARENT_CHILDRENS_CHILD_TABLE)
                .setValue(newIdList).await()
        }

        suspend fun updateChildCurrentGroupId(userChildId: String, groupId: String?) {
            GeneralRepositoryImpl.childsRef.child(userChildId).get().await()
                .getValue(UserChild::class.java)?.let {
                if (it.currentGroupId == groupId) {
                    throw Exception("У пользователя oldгруппа == новой присваеваемой")
                }
                GeneralRepositoryImpl.childsRef.child(userChildId)
                    .setValue(it.copy(currentGroupId = groupId))
            }
        }

        suspend fun getOldChildIdsList(parentId: String): List<String?> {
            val snapshot = GeneralRepositoryImpl.parentsRef.child(parentId)
                .child(FirebaseTables.PARENT_CHILDRENS_CHILD_TABLE).get().await()
            return snapshot.getValue<List<String>>() ?: listOf()
        }
    }
}