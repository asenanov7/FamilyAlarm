package com.example.familyalarm.utils

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FirebaseTables {
    companion object {
        val GENERAL_REF = Firebase.database(databaseUrl).getReference("General")
        val CHILDS_REF = Firebase.database(databaseUrl).getReference("Child's")
        val PARENTS_REF = Firebase.database(databaseUrl).getReference("Parents")

        const val CHILD_INVITES_CHILD_TABLE = "invitesParentsID"
        const val PARENT_CHILDRENS_CHILD_TABLE = "childrens"
    }

}

const val databaseUrl = "https://waketeam-75be8-default-rtdb.europe-west1.firebasedatabase.app"
