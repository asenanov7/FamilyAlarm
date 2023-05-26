package com.example.familyalarm.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.chesire.lifecyklelog.LogLifecykle
import com.example.familyalarm.databinding.InvitationsFragmentBinding
import com.example.familyalarm.presentation.recyclerview.InvitationsAdapter
import com.example.familyalarm.presentation.viewmodels.InvitationVM
import kotlinx.coroutines.launch

@LogLifecykle
class InvitationsFragment: Fragment() {

    private var _binding: InvitationsFragmentBinding? = null
    private val binding: InvitationsFragmentBinding
        get() = _binding ?: throw Exception("InvitationsFragment == null")

    private val adapter by lazy { InvitationsAdapter() }
    private val vm by lazy { ViewModelProvider(this)[InvitationVM::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        _binding = InvitationsFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView2.adapter = adapter

        adapter.clickAcceptInvite = {
            lifecycleScope.launch {
                vm.accept(parentId = it)
            }
        }

        lifecycleScope.launch {
            vm.invitations().collect{
                adapter.submitList(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    companion object{
        const val NAME = "InvitationsFragment"

        fun newInstance(): InvitationsFragment {
            return InvitationsFragment()
        }
    }
}