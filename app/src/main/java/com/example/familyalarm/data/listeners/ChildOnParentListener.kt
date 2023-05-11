package com.example.familyalarm.data.listeners

import android.util.Log
import com.example.familyalarm.data.impl_repositories.RepositoryImpl
import com.example.familyalarm.domain.entities.UserChild
import com.example.familyalarm.utils.FirebaseTables
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ChildOnParentListener(val repositoryImpl: RepositoryImpl, val scope: CoroutineScope) :
    ValueEventListener {

    private val parentsRef =
        Firebase.database(RepositoryImpl.databaseUrl)
            .getReference(FirebaseTables.PARENTS)

    fun addListener(parentId:String) {
        parentsRef.child(parentId).child("childrens")
            .addValueEventListener(this)
    }

    fun removeListener(parentId: String){
        parentsRef.child(parentId).child("childrens")
            .removeEventListener(this)
    }

    var isActive = false
    val result = MutableStateFlow(listOf<UserChild>())


    override fun onDataChange(snapshot: DataSnapshot) {
        isActive = true
        Log.d("ARSEN", "CHANGED $this")
        scope.launch {
            val listOfChilds = mutableListOf<UserChild>()
            for (item in snapshot.children) {
                item.getValue<String>()
                    ?.let { repositoryImpl.getUserChild(it) }
                    ?.let {
                        listOfChilds.add(it)
                    }
            }
            result.value = listOfChilds
        }
    }

    override fun onCancelled(error: DatabaseError) {
        isActive = false
        Log.d(RepositoryImpl.TAG, "onCancelled $error ")
        throw Exception(error.message)
    }


}