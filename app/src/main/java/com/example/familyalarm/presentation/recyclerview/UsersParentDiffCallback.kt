package com.example.familyalarm.presentation.recyclerview

import androidx.recyclerview.widget.DiffUtil
import com.example.familyalarm.domain.entities.UserParent

class UsersParentDiffCallback: DiffUtil.ItemCallback<UserParent>() {
    override fun areItemsTheSame(oldItem: UserParent, newItem: UserParent): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UserParent, newItem: UserParent): Boolean {
        return oldItem == newItem
    }
}