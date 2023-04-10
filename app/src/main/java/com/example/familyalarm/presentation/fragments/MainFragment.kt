package com.example.familyalarm.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.familyalarm.R
import com.example.familyalarm.databinding.MainFragmentBinding
import com.example.familyalarm.presentation.contract.navigator
import com.example.familyalarm.presentation.recyclerview.UsersAdapter
import com.example.familyalarm.presentation.viewmodels.MainVM
import com.example.familyalarm.utils.UiState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

    private var _binding: MainFragmentBinding? = null
    private val binding: MainFragmentBinding
        get() = _binding ?: throw Exception("MainFragment == null")

    private val vm by lazy { ViewModelProvider(this)[MainVM::class.java] }
    private val adapter by lazy { UsersAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        Log.d("MainFragment", "onCreateView: MainFragment $this")
        _binding = MainFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("MainFragment", "onViewCreated: MainFragment $this")
        //binding.textView.text = user

        binding.fabAdd.setOnClickListener {
            navigator().shouldLaunchFragment(
                InvitationsFragment.newInstance(),
                InvitationsFragment.NAME,
                true
            )
        }

        bottomNavigationListener()
        binding.recyclerView.adapter = adapter

        lifecycleScope.launch {
            vm.getUsersFromParentChildrens(Firebase.auth.currentUser!!.uid).collectLatest {
                adapter.submitList(it)
            }
        }
    }

    override fun onDestroyView() {
        Log.d("MainFragment", "onDestroyView: MainFragment $this")
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainFragment", "onDestroy: MainFragment $this")
    }

    private fun exit() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                Log.d("MainFragment", "binding.textViewExit.setOnClickListener")
                vm.logOut(requireContext()).collect {
                    Log.d("MainFragment", "ExitState: $it ")
                    when (it) {
                        UiState.Default -> {
                            binding.progressBarMain.isVisible = false
                        }
                        UiState.Loading -> {
                            binding.progressBarMain.isVisible = true
                        }
                        is UiState.Success -> {
                            navigator().shouldCloseFragment()
                            navigator().shouldLaunchFragment(
                                LoginFragment.newInstance(),
                                LoginFragment.NAME,
                                false
                            )
                        }
                        is UiState.Failure -> Toast.makeText(
                            requireContext(),
                            "Ошибка",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun bottomNavigationListener() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_exit -> {
                    exit()
                    Log.d("BottomNavigation", "bottomNavigationListener: EXit")
                    true
                }
                R.id.item_alarms -> {
                    Toast.makeText(requireContext(), "Будильники", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.item_groups -> {
                    false
                }
                else -> false
            }
        }
    }

    companion object {
        const val NAME: String = "MainFragment"

        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }
}