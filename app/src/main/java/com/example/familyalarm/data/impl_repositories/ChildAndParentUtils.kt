package com.example.familyalarm.data.impl_repositories

import android.util.Log
import com.example.familyalarm.domain.entities.UserChild
import com.example.familyalarm.utils.FirebaseTables
import com.example.familyalarm.utils.FirebaseTables.Companion.CHILDS_REF
import com.example.familyalarm.utils.FirebaseTables.Companion.PARENTS_REF
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.tasks.await

class ChildAndParentUtils {

    companion object {

        suspend fun addNewInvites(userChildId: String, parentId: String) {
            CHILDS_REF.child(userChildId).get().await().getValue(UserChild::class.java)?.let {
                val oldInvites = it.invitesParentsID ?: listOf()
                val newInvites = oldInvites.toMutableList()
                if (!newInvites.contains(parentId)) {
                    newInvites.add(parentId)
                }
                CHILDS_REF.child(userChildId).setValue(it.copy(invitesParentsID = newInvites))
            }
        }

        suspend fun getUserChild(userId: String): UserChild? {
            val snapshot = CHILDS_REF.child(userId).get().await()
            return snapshot.getValue(UserChild::class.java)
        }

        suspend fun removeInviteAfterAccept(userChildId: String, parentId: String) {
            CHILDS_REF.child(userChildId).get().await().getValue(UserChild::class.java)?.let {
                val oldInvites = it.invitesParentsID ?: listOf()
                Log.d("removeInviteAfterAccept", "oldInvites: $oldInvites ")
                val newInvites = oldInvites.toMutableList()
                if (newInvites.contains(parentId)) {
                    newInvites.remove(parentId)
                    Log.d("removeInviteAfterAccept", "newInvites($newInvites) remove: $parentId ")
                } else {
                    throw Exception("removeInviteAfterAccept newInvites not contains parentId for remove")
                }
                CHILDS_REF.child(userChildId).setValue(it.copy(invitesParentsID = newInvites))
            }
        }

        suspend fun setNewChildList(parentId: String, newIdList: List<String?>) {
            PARENTS_REF.child(parentId).child(FirebaseTables.PARENT_CHILDRENS_CHILD_TABLE)
                .setValue(newIdList).await()
        }

        suspend fun updateChildCurrentGroupId(userChildId: String, groupId: String?) {
            CHILDS_REF.child(userChildId).get().await()
                .getValue(UserChild::class.java)?.let {
                if (it.currentGroupId == groupId) {
                    throw Exception("У пользователя oldгруппа == новой присваеваемой")
                }
               //можно убрать верхнюю часть функции
                CHILDS_REF.child(userChildId).child("currentGroupId")
                    .setValue(groupId)
            }
        }

        suspend fun getOldChildIdsList(parentId: String): List<String?> {
            val snapshot = PARENTS_REF.child(parentId)
                .child(FirebaseTables.PARENT_CHILDRENS_CHILD_TABLE).get().await()
            return snapshot.getValue<List<String>>() ?: listOf()
        }
    }
}