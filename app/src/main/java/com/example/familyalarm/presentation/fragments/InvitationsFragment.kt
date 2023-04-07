package com.example.familyalarm.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView.OnQueryTextListener
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.familyalarm.databinding.InvitationsFragmentBinding
import com.example.familyalarm.databinding.MainFragmentBinding
import com.example.familyalarm.presentation.recyclerview.InvitationsAdapter
import com.example.familyalarm.presentation.viewmodels.InvitationsVM

class InvitationsFragment : Fragment() {

    private var _binding: InvitationsFragmentBinding? = null
    private val binding: InvitationsFragmentBinding
        get() = _binding ?: throw Exception("InvitationsFragment == null")


    private val vm by lazy { ViewModelProvider(this)[InvitationsVM::class.java] }
    private val adapter = InvitationsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("InvitationsFragment", "onCreateView: InvitationsFragment $this")
        _binding = InvitationsFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvInvitations.adapter = adapter

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                TODO()
            }
        })


    }

    override fun onDestroyView() {
        Log.d("InvitationsFragment", "onDestroyView: InvitationsFragment $this")
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        Log.d("InvitationsFragment", "onDestroy: InvitationsFragment $this")
        super.onDestroy()
    }

    companion object{
        const val NAME: String = "MainFragment"

        fun newInstance(): InvitationsFragment {
            return InvitationsFragment()
        }
    }
}