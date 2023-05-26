package com.example.familyalarm.data.impl_repositories

import com.example.familyalarm.data.listeners.SingleFirebaseListener
import com.example.familyalarm.domain.entities.User
import com.example.familyalarm.domain.entities.UserChild
import com.example.familyalarm.domain.entities.UserParent
import com.example.familyalarm.domain.repositories.GeneralRepository
import com.example.familyalarm.utils.FirebaseTables.CHILDS_TABLE
import com.example.familyalarm.utils.FirebaseTables.GENERAL_TABLE
import com.example.familyalarm.utils.FirebaseTables.PARENTS_TABLE
import com.example.familyalarm.utils.databaseUrl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

object GeneralRepositoryImpl : GeneralRepository {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val scope = CoroutineScope(Dispatchers.IO)


        val childsRef = Firebase.database(databaseUrl).getReference(CHILDS_TABLE)
        val parentsRef = Firebase.database(databaseUrl).getReference(PARENTS_TABLE)
        val generalRef = Firebase.database(databaseUrl).getReference(GENERAL_TABLE)



    private val childListener =
        object : SingleFirebaseListener<UserChild>(){

        override fun onDataChangeCustom(snapshot: DataSnapshot) {
            for (item in snapshot.children) {
                val userChild = item.getValue(UserChild::class.java)
                generalRef.child(userChild?.id ?: "null id").setValue(userChild)
            }
        }

        override fun onCanceledCustom(error: DatabaseError) {
            throw Exception("childListener on GeneralRepo onCancelled: $error ")
        }
    }
    private val parentListener =
        object :SingleFirebaseListener<UserParent>(){

        override fun onDataChangeCustom(snapshot: DataSnapshot) {
            for (item in snapshot.children) {
                val userParent = item.getValue(UserParent::class.java)
                generalRef.child(userParent?.id ?: "null id").setValue(userParent)
            }
        }

        override fun onCanceledCustom(error: DatabaseError) {
            throw Exception("parentListener on GeneralRepo onCancelled: $error ")
        }
    }



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

    override suspend fun getUserInfo(userId: String): User {
        return try {
            childsRef.child(userId).get().await().getValue<UserChild>()!!
        } catch (e: java.lang.Exception) {
            parentsRef.child(userId).get().await().getValue<UserParent>()!!
        }
    }


    fun setGeneralAutoChangeListener() {
        if (!childListener.isActive){
            childListener.attachListener(childsRef)
        }

        if (!parentListener.isActive){
            parentListener.attachListener(parentsRef)
        }
    }


}


