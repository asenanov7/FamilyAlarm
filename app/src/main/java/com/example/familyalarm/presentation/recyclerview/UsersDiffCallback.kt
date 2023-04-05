package com.example.familyalarm.presentation.recyclerview

import androidx.recyclerview.widget.DiffUtil
import com.example.familyalarm.domain.entities.UserChild

class UsersDiffCallback: DiffUtil.ItemCallback<UserChild>() {
    override fun areItemsTheSame(oldItem: UserChild, newItem: UserChild): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UserChild, newItem: UserChild): Boolean {
        return oldItem == newItem
    }
}