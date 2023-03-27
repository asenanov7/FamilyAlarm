package com.example.familyalarm.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.familyalarm.R
import com.example.familyalarm.databinding.ResetPasswordFragmentBinding
import com.example.familyalarm.presentation.viewmodels.ResetPasswordVM
import com.example.familyalarm.presentation.viewmodels.ResetPasswordVMState
import com.example.familyalarm.presentation.viewmodels.ResetPasswordVMState.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ResetPasswordFragment : Fragment() {

    private var _binding: ResetPasswordFragmentBinding? = null
    private val binding: ResetPasswordFragmentBinding
        get() = _binding ?: throw Exception("ResetPasswordFragment == null")

    private val vm by lazy {
        ViewModelProvider(this)[ResetPasswordVM::class.java]
    }

    interface ShouldCloseFragmentListener {
        fun shouldCloseFragment()
    }

    private lateinit var shouldCloseFragmentBridge: ShouldCloseFragmentListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ShouldCloseFragmentListener) {
            shouldCloseFragmentBridge = context
        } else throw Exception(
            "If Activity use ResetPasswordFragment, activity should implement ShouldCloseFragmentListener"
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = ResetPasswordFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            vm.stateFlow.collect {
                when (it) {
                    Normal -> {
                        binding.progressBar.isVisible = false
                        binding.buttonReset.isEnabled = true
                    }
                    Loading -> {
                        binding.progressBar.isVisible = true
                        binding.buttonReset.isEnabled = false
                    }
                    Success -> {
                        binding.progressBar.isVisible = false
                        binding.buttonReset.isEnabled = true
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.sended),
                            Toast.LENGTH_SHORT
                        ).show()
                        shouldCloseFragmentBridge.shouldCloseFragment()
                    }
                    Failure -> {
                        binding.progressBar.isVisible = false
                        binding.buttonReset.isEnabled = true
                    }
                }
            }
        }

        binding.buttonReset.setOnClickListener {
            lifecycleScope.launch {
                vm.checkValidAndReset(binding.textInputEditTextEmail, binding.textInputLayoutEmail)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun makeResetPasswordFragment(): ResetPasswordFragment {
            return ResetPasswordFragment()
        }
    }
}