package com.example.familyalarm.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.familyalarm.R
import com.example.familyalarm.databinding.ResetPasswordFragmentBinding
import com.example.familyalarm.presentation.Navigation
import com.example.familyalarm.presentation.viewmodels.ResetPasswordVM
import com.example.familyalarm.utils.UiState.*
import com.example.familyalarm.utils.showErrorWithDisappearance
import kotlinx.coroutines.launch

class ResetPasswordFragment : Fragment() {

    private var _binding: ResetPasswordFragmentBinding? = null
    private val binding: ResetPasswordFragmentBinding
        get() = _binding ?: throw Exception("ResetPasswordFragment == null")

    private val vm by lazy {
        ViewModelProvider(this)[ResetPasswordVM::class.java]
    }

    private lateinit var navigation: Navigation

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Navigation) {
            navigation = context
        } else throw Exception(
            "If Activity use ResetPasswordFragment, activity should implement Navigation"
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        Log.d("ResetPasswordFragment", "onCreateView: ResetFragment $this")
        _binding = ResetPasswordFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        lifecycleScope.launch {
            observeVmState()
        }

        binding.buttonReset.setOnClickListener {
            hideKeyboard()
            resetPassword()
        }
    }


    private suspend fun observeVmState(){
        vm.stateFlow.collect {
            when (it) {
                Init -> {
                    binding.progressBar.isVisible = false
                    binding.buttonReset.isEnabled = true
                }
                Loading -> {
                    binding.progressBar.isVisible = true
                    binding.buttonReset.isEnabled = false
                }
                is Success -> {
                    binding.progressBar.isVisible = false
                    binding.buttonReset.isEnabled = true
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.sended),
                        Toast.LENGTH_SHORT
                    ).show()
                    navigation.shouldCloseFragment()
                }
                is Failure -> {
                    binding.progressBar.isVisible = false
                    binding.buttonReset.isEnabled = true
                    showErrorWithDisappearance(
                        binding.textviewErrors, it.exceptionMessage, 5000
                    )
                }
            }
        }
    }

    override fun onDestroyView() {
        Log.d("ResetPasswordFragment", "onDestroyView: ResetFragment $this")
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        Log.d("ResetPasswordFragment", "onDestroyView: ResetFragment $this")
        super.onDestroy()
    }

    private fun resetPassword(){
        val email = binding.textInputEditTextEmail.text.toString().trim()
        if (email.isNotEmpty()) {
            lifecycleScope.launch {
                vm.reset(email)
            }
        }
    }

    private fun hideKeyboard(){
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (requireActivity().currentFocus !=null){
            inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
        }

        // on below line hiding our keyboard.
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    companion object {

        const val NAME = "ResetPasswordFragment"
        fun newInstance(): ResetPasswordFragment {
            return ResetPasswordFragment()
        }
    }
}