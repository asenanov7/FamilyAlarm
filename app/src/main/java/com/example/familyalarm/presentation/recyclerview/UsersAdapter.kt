package com.example.familyalarm.presentation.recyclerview

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import com.example.familyalarm.R
import com.example.familyalarm.databinding.ItemUserChildBinding
import com.example.familyalarm.domain.entities.UserChild


class UsersAdapter : ListAdapter <UserChild, UserChildViewHolder> (UsersChildDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserChildViewHolder {
        val binding = ItemUserChildBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return UserChildViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserChildViewHolder, position: Int) {
        val user = getItem(position)

        holder.binding.textViewName.text = user.name.toString()
        holder.binding.textViewEmail.text = user.email.toString()
        holder.binding.imageViewAwake.setImageDrawable(getAwakeStatus(user, holder))
        holder.binding.imageViewAvatar //////

    }

    private fun getAwakeStatus(userChild: UserChild, holder: UserChildViewHolder): Drawable? {
        return if (userChild.awake) {
            ContextCompat.getDrawable(
                holder.itemView.context,
                R.drawable.status_connected_svgrepo_com
            )
        } else {
            ContextCompat.getDrawable(
                holder.itemView.context,
                R.drawable.status_disconnected_svgrepo_com
            )
        }
    }
}