package com.example.familyalarm.data.listeners

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow


abstract class SingleFirebaseListener<T> : ValueEventListener {

    inner class ListenerInfo(
        val listener: SingleFirebaseListener<T>,
        val firebaseRef: DatabaseReference,
    )

    var isActive = false

    open var result: MutableStateFlow<List<T>>? = null


    private val listOfAttachedListenersInfo =
        mutableListOf<ListenerInfo>()

    abstract fun onDataChangeCustom(snapshot: DataSnapshot)

    abstract fun onCanceledCustom(error: DatabaseError)


    fun attachListener(
        firebaseDatabaseReference: DatabaseReference,
        vararg childs: String
    ) {
        val reference: DatabaseReference = when (childs.size) {
            0 -> firebaseDatabaseReference
            1 -> firebaseDatabaseReference.child(childs[0])
            2 -> firebaseDatabaseReference.child(childs[0]).child(childs[1])
            3 -> firebaseDatabaseReference.child(childs[0]).child(childs[1]).child(childs[2])
            else -> throw Exception("attachListener: Unknown reference")
        }
        reference.addValueEventListener(this)
        isActive = true

        Log.d("SingleFirebaseListener", "listener add $this ")
        listOfAttachedListenersInfo.add(ListenerInfo(this, reference))
    }

    fun detachListener(
        firebaseDatabaseReference: DatabaseReference,
        vararg childs: String
    ) {
        val reference: DatabaseReference = when (childs.size) {
            0 -> firebaseDatabaseReference
            1 -> firebaseDatabaseReference.child(childs[0])
            2 -> firebaseDatabaseReference.child(childs[0]).child(childs[1])
            3 -> firebaseDatabaseReference.child(childs[0]).child(childs[1]).child(childs[2])
            else -> throw Exception("attachListener: Unknown reference")
        }

        reference.removeEventListener(this)
        listOfAttachedListenersInfo.remove(ListenerInfo(this, reference))
        isActive = false

    }

    fun detachAllListeners() {
        listOfAttachedListenersInfo.forEach {
            it.listener.detachListener(it.firebaseRef)
        }
    }

    override fun onDataChange(snapshot: DataSnapshot) {
        onDataChangeCustom(snapshot)
    }

    override fun onCancelled(error: DatabaseError) {
        isActive = false
        onCanceledCustom(error)
    }



}