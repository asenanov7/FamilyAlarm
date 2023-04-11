package com.example.familyalarm.presentation.recyclerview

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.familyalarm.databinding.ItemUserSearchAddBinding
import com.example.familyalarm.databinding.ItemUserSearchAddedBinding
import com.example.familyalarm.domain.entities.UserChild
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SearchAdapter : ListAdapter<UserChild, RecyclerView.ViewHolder>(UsersDiffCallback()) {

    companion object {
        const val UNKNOWN_HOLDER = 0
        const val ADD_HOLDER = 1
        const val ADDED_HOLDER = 2
    }

    lateinit var clickAddUser: (userId: String) -> Unit

    override fun getItemViewType(position: Int): Int {
        var result = UNKNOWN_HOLDER
        val userChild = getItem(position)
        val parentUserId =
            Firebase.auth.currentUser?.uid ?: throw java.lang.Exception("Parent==null")
        if (userChild.invitesParentsID!=null && userChild.invitesParentsID.contains(parentUserId)) {
            result = ADDED_HOLDER
        } else if (userChild.invitesParentsID == null) {
            result = ADD_HOLDER
        }
        return result
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val bindingAdd =
            ItemUserSearchAddBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        val bindingAdded =
            ItemUserSearchAddedBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        val holder = when (viewType) {
            ADD_HOLDER -> AddUserViewHolder(bindingAdd)
            ADDED_HOLDER -> AddedUserViewHolder(bindingAdded)
            else -> {
                throw Exception("UNKNOWN HOLDER")
            }
        }

        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val user = getItem(position)

        when (holder) {
            is AddUserViewHolder -> {
                Log.d("ADAPTER", "ADD: ")
                holder.binding.textViewName.text = user.name.toString()
                holder.binding.textViewEmail.text = user.email.toString()
                holder.binding.imageViewAvatar //////////
                holder.binding.imageViewButtonAdd.setOnClickListener {
                    clickAddUser(user?.id ?: throw Exception("UserID==null"))
                }
            }
            is AddedUserViewHolder -> {
                Log.d("ADAPTER", "ADDED: ")
                holder.binding.textViewName.text = user.name.toString()
                holder.binding.textViewEmail.text = user.email.toString()
                holder.binding.imageViewAvatar      /////
            }
        }
    }

    private fun click(onClicked: () -> Unit, view: View) {
        view.alpha = 0.1f
    }


}
