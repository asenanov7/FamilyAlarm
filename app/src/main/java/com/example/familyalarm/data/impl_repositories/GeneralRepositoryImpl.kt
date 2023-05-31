package com.example.familyalarm.data.impl_repositories


import android.util.Log
import com.example.familyalarm.domain.entities.User
import com.example.familyalarm.domain.entities.UserChild
import com.example.familyalarm.domain.entities.UserParent
import com.example.familyalarm.domain.repositories.GeneralRepository
import com.example.familyalarm.utils.FirebaseTables
import com.example.familyalarm.utils.FirebaseTables.Companion.CHILDS_REF
import com.example.familyalarm.utils.FirebaseTables.Companion.GENERAL_REF
import com.example.familyalarm.utils.FirebaseTables.Companion.PARENTS_REF
import com.example.familyalarm.utils.throwEx
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

 class GeneralRepositoryImpl private constructor() : GeneralRepository {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val scope = CoroutineScope(Dispatchers.IO)


    private val childListener = object : ValueEventListener {

        override fun onDataChange(snapshot: DataSnapshot) {
            Log.d("FIX_LISTENERS_BUG", "CHANGED on ${this@GeneralRepositoryImpl} listener = $this")
            for (item in snapshot.children) {
                val userChild = item.getValue(UserChild::class.java)
                GENERAL_REF.child(userChild?.id ?: "null id").setValue(userChild)
            }
        }

        override fun onCancelled(error: DatabaseError) {
            throwEx(onCancelled(error))
        }

    }

    private val parentListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            Log.d("FIX_LISTENERS_BUG", "CHANGED on ${this@GeneralRepositoryImpl} listener = $this")
            for (item in snapshot.children) {
                val userParent = item.getValue(UserParent::class.java)
                GENERAL_REF.child(userParent?.id ?: "null id").setValue(userParent)
            }
        }

        override fun onCancelled(error: DatabaseError) {
            throwEx(onCancelled(error))
        }
    }

    fun setGeneralAutoChangeListener() {
        CHILDS_REF.addValueEventListener(childListener)
        PARENTS_REF.addValueEventListener(parentListener)
    }

    override suspend fun createChild(userChild: UserChild) {
        scope.launch {
            auth.currentUser?.let { CHILDS_REF.child(it.uid).setValue(userChild) }
        }.join()
    }

    override suspend fun createParent(userParent: UserParent) {
        scope.launch {
            auth.currentUser?.let { PARENTS_REF.child(it.uid).setValue(userParent) }
        }.join()
    }

    override suspend fun getUserInfo(userId: String): User {
        return try {
            CHILDS_REF.child(userId).get().await().getValue<UserChild>()!!
        } catch (e: java.lang.Exception) {
            PARENTS_REF.child(userId).get().await().getValue<UserParent>()!!
        }
    }

    companion object{

        private var INSTANCE: GeneralRepositoryImpl? = null
        private val LOCK = Any()

        fun create(): GeneralRepositoryImpl{
            INSTANCE?.let { return it }

            synchronized(LOCK){
                INSTANCE?.let { return it }
                val generalRepImpl = GeneralRepositoryImpl()
                INSTANCE = generalRepImpl
                return generalRepImpl
            }
        }

        fun destroy(){
            Log.d("FIX_LISTENERS_BUG", "destroy: ${CHILDS_REF.removeEventListener(INSTANCE?.childListener!!)}")
            Log.d("FIX_LISTENERS_BUG", "destroy: ${PARENTS_REF.removeEventListener(INSTANCE?.parentListener!!)}")
            CHILDS_REF.removeEventListener(INSTANCE?.childListener!!)
            PARENTS_REF.removeEventListener(INSTANCE?.parentListener!!)
            INSTANCE = null
        }


    }


}


