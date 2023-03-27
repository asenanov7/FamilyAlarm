package com.example.familyalarm.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.familyalarm.R
import com.example.familyalarm.databinding.LoginFragmentBinding
import com.example.familyalarm.presentation.viewmodels.LoginVM
import com.example.familyalarm.utils.UiState.*
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private val vm by lazy { ViewModelProvider(this)[LoginVM::class.java] }

    private var _binding: LoginFragmentBinding? = null
    private val binding: LoginFragmentBinding
        get() = _binding ?: throw Exception("LoginFragment == null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = LoginFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.clearErrorOnInputChanged(
            binding.textInputEditTextEmail,
            binding.textInputEditTextPassword
        )

        lifecycleScope.launch {
           observeVmState()
        }

        binding.buttonSignUp.setOnClickListener {

            lifecycleScope.launch {
                val email = binding.textInputEditTextEmail.text.toString().trim()
                val password = binding.textInputEditTextPassword.text.toString()

                if (email.isNotEmpty() && password.isNotEmpty()) {
                    vm.login(email, password)
                }
            }
        }

        binding.forgotPass.setOnClickListener{
            launchResetPasswordFragment()
        }
    }

    private suspend fun observeVmState(){
        vm.stateFlow.collect {
            Log.d("SENANOV", "observeVmState: $it")
            when (it) {
                Init -> {
                    binding.textInputLayoutEmail.error = null
                    binding.textInputLayoutPassword.error = null
                    binding.progressBar.isVisible = false
                    binding.buttonSignUp.isEnabled = true
                }
                Loading -> {
                    binding.progressBar.isVisible = true
                    binding.buttonSignUp.isEnabled = false
                }
                is Success -> {
                    binding.progressBar.isVisible = false
                    binding.buttonSignUp.isEnabled = true
                }
                is Failure -> {
                    binding.progressBar.isVisible = false
                    binding.buttonSignUp.isEnabled = true
                    binding.textInputLayoutPassword.error = it.error
                }
            }
        }
    }

    private fun launchResetPasswordFragment(){
        val fragment = ResetPasswordFragment.makeResetPasswordFragment()

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment)
            .addToBackStack(NAME)
            .commit()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val NAME = "LoginFragment"

        fun makeLoginFragment(): LoginFragment {
            return LoginFragment()
        }
    }
}