package com.example.familyalarm.presentation.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.familyalarm.databinding.ItemUserParentInviteBinding
import com.example.familyalarm.domain.entities.UserParent

class InvitationsAdapter: ListAdapter<UserParent, UserParentViewHolder>(UsersParentDiffCallback()){

    lateinit var clickAcceptInvite: (parentId: String) -> Unit

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserParentViewHolder {
            val binding = ItemUserParentInviteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return UserParentViewHolder(binding)
        }

        override fun onBindViewHolder(holder: UserParentViewHolder, position: Int) {
            val user = getItem(position)

            holder.binding.textViewName.text = user.name.toString()
            holder.binding.textViewEmail.text = user.email.toString()
            holder.binding.imageViewAvatar //////

            holder.binding.buttonAccept.setOnClickListener {
                clickAcceptInvite(user.id?:"user id == null")
            }

        }
}