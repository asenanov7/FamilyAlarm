package com.example.familyalarm.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.allViews
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.chesire.lifecyklelog.LogLifecykle
import com.example.familyalarm.R
import com.example.familyalarm.databinding.ResetPasswordFragmentBinding
import com.example.familyalarm.presentation.contract.navigator
import com.example.familyalarm.presentation.viewmodels.ResetPasswordVM
import com.example.familyalarm.utils.UiState.*
import com.example.familyalarm.utils.showErrorWithDisappearance
import kotlinx.coroutines.launch

@LogLifecykle
class ResetPasswordFragment : Fragment() {

    private var _binding: ResetPasswordFragmentBinding? = null
    private val binding: ResetPasswordFragmentBinding
        get() = _binding ?: throw Exception("ResetPasswordFragment == null")

    private val vm by lazy {
        ViewModelProvider(this)[ResetPasswordVM::class.java]
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
            observeVmState()
        }

        binding.buttonReset.setOnClickListener {
            hideKeyboard()
            resetPassword()
        }
    }


    private suspend fun observeVmState(){
        repeatOnLifecycle(Lifecycle.State.RESUMED) {
            vm.stateFlow.collect {
                Log.d("ResetPasswordFragment", "ResetPasswordState: $it ")
                when (it) {
                    Default -> {
                        binding.progressBar.isVisible = false
                        binding.root.allViews.forEach {view-> view.isEnabled = true }
                    }
                    Loading -> {
                        binding.progressBar.isVisible = true
                        binding.root.allViews.forEach {view-> view.isEnabled = false }
                    }
                    is Success -> {
                        binding.progressBar.isVisible = false
                        binding.root.allViews.forEach {view-> view.isEnabled = true }
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.sended),
                            Toast.LENGTH_SHORT
                        ).show()
                        navigator().shouldCloseFragment()
                    }
                    is Failure -> {
                        binding.progressBar.isVisible = false
                        binding.root.allViews.forEach {view-> view.isEnabled = true }
                        showErrorWithDisappearance(
                            binding.textviewErrors, it.exceptionMessage, 5000
                        )
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun resetPassword(){
        val email = binding.textInputEditTextEmail.text.toString().trim()
        if (email.isNotEmpty()) {
            lifecycleScope.launch {
                vm.reset(email, requireContext())
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