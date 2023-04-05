package com.example.familyalarm.data.impl_repositories

import android.util.Log
import com.example.familyalarm.data.Mapper
import com.example.familyalarm.domain.entities.Group
import com.example.familyalarm.domain.entities.User
import com.example.familyalarm.domain.entities.UserChild
import com.example.familyalarm.domain.entities.UserParent
import com.example.familyalarm.domain.repositories.Repository
import com.example.familyalarm.utils.FirebaseTables
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class RepositoryImpl() : Repository {

    private val mapper = Mapper()
    private val auth by lazy { FirebaseAuth.getInstance() }


    private val parentGroupsRef =
        Firebase.database("https://waketeam-75be8-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference(FirebaseTables.PARENT_GROUPS)
    private val childsRef =
        Firebase.database("https://waketeam-75be8-default-rtdb.europe-west1.firebasedatabase.app")
            .getReference(FirebaseTables.CHILDS)

    private val parentsRef = Firebase.database("https://waketeam-75be8-default-rtdb.europe-west1.firebasedatabase.app")
        .getReference(FirebaseTables.PARENTS)

    private val scope = CoroutineScope(Dispatchers.IO)
    override suspend fun createChildUseCase(userChild: UserChild) {
        Log.d("Database", "createChildUseCase: started")
        val job = scope.launch {
            auth.currentUser?.let {
                Log.d("Database", "createChildUseCase: currentUser!=null")
                childsRef
                    .child(it.uid)
                    .setValue(userChild)
                    .addOnSuccessListener { Log.d("Database", "createChildUseCase: user Success") }
                    .addOnFailureListener { Log.d("Database", "createChildUseCase: user Fail") }
            }
        }
        job.join()
}

override suspend fun createParentUseCase(userParent: UserParent) {
    Log.d("Database", "createParentUseCase: started")
    auth.currentUser?.let { firebaseUser ->
        Log.d("Database", "createParentUseCase: currentUser!=null")
        val group = Group(firebaseUser.uid, userParent, listOf())

        val job = scope.launch() {
            parentGroupsRef
                .child(firebaseUser.uid)
                .setValue(group)
                .addOnSuccessListener {
                    Log.d("Database", "createParentUseCase: group: Success")
                    parentsRef
                        .child(firebaseUser.uid)
                        .setValue(userParent)
                        .addOnSuccessListener {
                            Log.d(
                                "Database",
                                "createParentUseCase: user Success"
                            )
                        }
                        .addOnFailureListener {
                            Log.d(
                                "Database",
                                "createParentUseCase: user Fail"
                            )
                        }
                }
                .addOnFailureListener { Log.d("Database", "createParentUseCase: group: Fail") }
        }
        job.join()
    }
}

override fun getUsersFromTheGroup(userGroupId: String): MutableSharedFlow<List<UserChild>> {
    val group = parentGroupsRef.child(userGroupId).child("usersInGroup")

    val listUserMutableFlow = MutableSharedFlow<List<UserChild>>(replay = 1)

    group.addValueEventListener(object : ValueEventListener {

        override fun onDataChange(snapshot: DataSnapshot) {
            var listUser = listOf<UserChild>()
            Log.d("Database", "onDataChange $snapshot")
            for (item in snapshot.children) {
                Log.d("Database", "(item in snapshot.children ")
                val user = item.getValue(UserChild::class.java)
                if (user != null) {
                    Log.d("Database", "user != null ")
                    val newList = listUser.toMutableList()
                    newList.add(user)
                    listUser = newList
                }
                Log.d("Database", "listResult: $listUser")
                scope.launch {
                    listUserMutableFlow.emit(listUser)
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.d("Database", "onCancelled $error ")
            throw Exception(error.message)
        }
    })

    return listUserMutableFlow
}

override fun inviteUserInTheGroup(userId: String, userGroupId: String) {
    TODO("Not yet implemented")
}

override fun deleteUserFromGroup(userId: String, userGroupId: String) {
    TODO("Not yet implemented")
}

override fun getUserInfo(userId: String): Flow<User> {
    TODO("Not yet implemented")
}


}