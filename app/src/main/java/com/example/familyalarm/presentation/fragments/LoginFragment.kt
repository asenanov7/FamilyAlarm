package com.example.familyalarm.presentation.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.doBeforeTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.familyalarm.R
import com.example.familyalarm.databinding.LoginFragmentBinding
import com.example.familyalarm.presentation.viewmodels.LoginVM
import com.example.familyalarm.presentation.viewmodels.LoginVMState
import com.example.familyalarm.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        lifecycleScope.launch {
           observeVmState()
        }

        binding.buttonSignUp.setOnClickListener {

            lifecycleScope.launch {
               vm.checkErrorsAndLogin(
                   binding.textInputEditTextEmail,
                   binding.textInputLayoutEmail,
                   binding.textInputEditTextPassword,
                   binding.textInputLayoutPassword
               )
            }
        }
    }

    private suspend fun observeVmState(){
        vm.stateFlow.collect {
            Log.d("ARSEN", "LoginVmState: $it")
            when (it) {
                LoginVMState.Normal -> {
                    binding.progressBar.isVisible = false
                    binding.buttonSignUp.isEnabled = true
                }
                LoginVMState.Loading -> {
                    binding.progressBar.isVisible = true
                    binding.buttonSignUp.isEnabled = false
                }
                LoginVMState.Success -> {
                    binding.progressBar.isVisible = false
                    binding.buttonSignUp.isEnabled = true
                }
                LoginVMState.Failure -> {
                    binding.progressBar.isVisible = false
                    binding.buttonSignUp.isEnabled = true
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun makeLoginFragment(): LoginFragment {
            return LoginFragment()
        }
    }
}