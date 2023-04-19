package com.example.familyalarm.presentation.recyclerview

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.familyalarm.databinding.ItemUserChildBinding
import com.example.familyalarm.databinding.ItemUserParentInviteBinding
import com.example.familyalarm.databinding.ItemUserSearchAddBinding
import com.example.familyalarm.databinding.ItemUserSearchAddedBinding

class UserChildViewHolder(val binding: ItemUserChildBinding) : ViewHolder(binding.root)
class UserParentViewHolder(val binding: ItemUserParentInviteBinding) : ViewHolder(binding.root)

class InviteUserViewHolder(val binding: ItemUserSearchAddBinding) : ViewHolder(binding.root)

class InvitedUserViewHolder(val binding: ItemUserSearchAddedBinding) : ViewHolder(binding.root)