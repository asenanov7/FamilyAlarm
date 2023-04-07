package com.example.familyalarm.presentation.recyclerview

import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.familyalarm.databinding.ItemUserChildBinding
import com.example.familyalarm.databinding.ItemUserSearchAddBinding
import com.example.familyalarm.databinding.ItemUserSearchAddedBinding

class UserViewHolder(val binding: ItemUserChildBinding) : ViewHolder(binding.root)

class AddUserViewHolder(val binding: ItemUserSearchAddBinding) : ViewHolder(binding.root)

class AddedUserViewHolder(val binding: ItemUserSearchAddedBinding) : ViewHolder(binding.root)