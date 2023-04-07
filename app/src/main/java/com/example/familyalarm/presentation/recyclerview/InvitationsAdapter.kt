package com.example.familyalarm.presentation.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.familyalarm.databinding.ItemUserSearchAddBinding
import com.example.familyalarm.databinding.ItemUserSearchAddedBinding
import com.example.familyalarm.domain.entities.UserChild

class InvitationsAdapter : ListAdapter<UserChild, RecyclerView.ViewHolder>(UsersDiffCallback()) {

    companion object {
        const val ADD_HOLDER = 1
        const val ADDED_HOLDER = 2
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
                holder.binding.textViewName.text = user.name.toString()
                holder.binding.textViewEmail.text = user.email.toString()
                holder.binding.imageViewAvatar //////
            }
            is AddedUserViewHolder -> {
                holder.binding.textViewName.text = user.name.toString()
                holder.binding.textViewEmail.text = user.email.toString()
                holder.binding.imageViewAvatar      /////
            }
        }
    }

}
