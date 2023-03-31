package com.example.familyalarm.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.familyalarm.databinding.MainFragmentBinding
import com.example.familyalarm.presentation.Navigation
import com.example.familyalarm.presentation.viewmodels.MainVM
import com.example.familyalarm.utils.UiState
import com.example.familyalarm.utils.showErrorWithDisappearance
import kotlinx.coroutines.launch

class MainFragment: Fragment() {

    private var _binding: MainFragmentBinding? = null
    private val binding: MainFragmentBinding
        get() = _binding ?: throw Exception("MainFragment == null")

    private val vm by lazy { ViewModelProvider(this)[MainVM::class.java] }


    private lateinit var navigation:Navigation
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Navigation) {
            navigation = context
        } else throw Exception(
            "If Activity use MainFragment, activity should implement Navigation"
        )
    }


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

        lifecycleScope.launch {
            vm.stateFlow.collect {
                when (it) {
                    UiState.Init -> {binding.progressBarMain.isVisible = true}
                    UiState.Loading -> {binding.progressBarMain.isVisible = true}
                    is UiState.Success -> {
                        navigation.shouldCloseFragment()
                        navigation.shouldLaunchFragment(LoginFragment.newInstance(), LoginFragment.NAME, false)
                    }
                    is UiState.Failure -> Toast.makeText(requireContext(), "Ошибка", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.textViewExit.setOnClickListener {
            lifecycleScope.launch {
                vm.logOut(requireContext())
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

    companion object{
        const val NAME: String = "MainFragment"

        fun newInstance():MainFragment {
            return MainFragment()
        }
    }
}