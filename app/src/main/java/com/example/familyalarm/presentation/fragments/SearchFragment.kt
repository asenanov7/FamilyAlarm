package com.example.familyalarm.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.familyalarm.databinding.SearchFragmentBinding
import com.example.familyalarm.presentation.recyclerview.SearchAdapter
import com.example.familyalarm.presentation.viewmodels.SearchVM
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private var _binding: SearchFragmentBinding? = null
    private val binding: SearchFragmentBinding
        get() = _binding ?: throw Exception("InvitationsFragment == null")


    private val vm by lazy { ViewModelProvider(this)[SearchVM::class.java] }
    private val adapter by lazy { SearchAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("InvitationsFragment", "onCreateView: InvitationsFragment $this")
        _binding = SearchFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("InvitationsFragment", "onViewCreated: InvitationsFragment $this")
        binding.rvInvitations.adapter = adapter
        adapter.clickAddUser = {
            lifecycleScope.launch {
                val result = vm.invite(it)
                if (result) {
                    val text = binding.searchView.query
                    vm.getUsersByHazyName(text.toString().trim()).collect {
                        Log.d("InvitationsFragment", "  vm.getUsersByHazyName().collect: $it")
                        adapter.submitList(it)
                    }
                } else {
                    Toast.makeText(requireContext(), "Ошибка", Toast.LENGTH_SHORT).show()
                }
            }
        }

            binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(p0: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(p0: String?): Boolean {
                    if (p0.isNullOrEmpty()) {
                        //Пустой лист при первом запуске и в случае стирания
                        adapter.submitList(listOf())
                        return false
                    }

                    lifecycleScope.launch {
                        vm.getUsersByHazyName(p0.trim()).collect {
                            Log.d("InvitationsFragment", "  vm.getUsersByHazyName().collect: $it")
                            adapter.submitList(it)
                        }
                    }
                    return false
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

    companion object {
        const val NAME: String = "MainFragment"

        fun newInstance(): SearchFragment {
            return SearchFragment()
        }
    }
}