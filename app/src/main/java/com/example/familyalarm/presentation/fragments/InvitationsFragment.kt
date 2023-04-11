package com.example.familyalarm.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.familyalarm.databinding.InvitationsFragmentBinding
import com.example.familyalarm.databinding.MainFragmentBinding

class InvitationsFragment: Fragment() {

    private var _binding: InvitationsFragmentBinding? = null
    private val binding: InvitationsFragmentBinding
        get() = _binding ?: throw Exception("InvitationsFragment == null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        Log.d("InvitationsFragment", "onCreateView: InvitationsFragment $this")
        _binding = InvitationsFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("InvitationsFragment", "onViewCreated: InvitationsFragment $this")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        Log.d("InvitationsFragment", "onDestroyView: InvitationsFragment $this")
        super.onDestroyView()
    }

    override fun onDestroy() {
        Log.d("InvitationsFragment", "onDestroy: InvitationsFragment $this")
        super.onDestroy()
    }

    companion object{
        const val NAME = "InvitationsFragment"

        fun newInstance(): InvitationsFragment {
            return InvitationsFragment()
        }
    }
}