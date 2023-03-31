package com.example.familyalarm.presentation.fragments

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.familyalarm.databinding.LoginFragmentBinding
import com.example.familyalarm.presentation.Navigation
import com.example.familyalarm.presentation.viewmodels.LoginVM
import com.example.familyalarm.utils.UiState.*
import com.example.familyalarm.utils.showErrorWithDisappearance
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private val vm by lazy { ViewModelProvider(this)[LoginVM::class.java] }

    private var _binding: LoginFragmentBinding? = null
    private val binding: LoginFragmentBinding
        get() = _binding ?: throw Exception("LoginFragment == null")

    private lateinit var navigation: Navigation

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Navigation) {
            navigation = context
        } else throw Exception(
            "If Activity use LoginFragment, activity should implement Navigation"
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("LoginFragment", "onCreateView: LoginFragment $this")
        _binding = LoginFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
           observeVmState(savedInstanceState)
        }

        binding.buttonSignUp.setOnClickListener {
            hideKeyboard()
            login()
        }

        binding.forgotPass.setOnClickListener{
            navigation.shouldLaunchFragment(
                ResetPasswordFragment.newInstance(),
                ResetPasswordFragment.NAME,
                true
            )
        }

        binding.textViewRegister.setOnClickListener{
            navigation.shouldLaunchFragment(
                RegisterFragment.newInstance(),
                RegisterFragment.NAME,
                true
            )
        }
    }


    private fun hideKeyboard(){
        val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        if (requireActivity().currentFocus !=null){
            inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
        }

        // on below line hiding our keyboard.
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun login(){
        lifecycleScope.launch {
            val email = binding.textInputEditTextEmail.text.toString().trim()
            val password = binding.textInputEditTextPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                vm.login(email, password)
            }
        }
    }

    private suspend fun observeVmState(savedInstanceState: Bundle?){
        vm.stateFlow.collect {
            Log.d("LoginFragment", "loginVmState: $it")
            when (it) {
                Init -> {
                    binding.progressBar.isVisible = false
                    binding.buttonSignUp.isEnabled = true
                }
                Loading -> {
                    binding.progressBar.isVisible = true
                    binding.buttonSignUp.isEnabled = false
                }
                is Success -> {
                    navigation.shouldCloseFragment()
                    navigation.shouldLaunchFragment(
                        MainFragment.newInstance(), MainFragment.NAME, false
                )
                }
                is Failure -> {
                    binding.progressBar.isVisible = false
                    binding.buttonSignUp.isEnabled = true
                        showErrorWithDisappearance(
                            binding.textviewErrors, it.exceptionMessage, 5000
                        )
                }
            }
        }
    }


    override fun onDestroyView() {
        Log.d("LoginFragment", "onDestroyView: LoginFragment $this")
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        Log.d("LoginFragment", "onDestroy: LoginFragment $this")
        super.onDestroy()
    }

    companion object {

        const val NAME = "LoginFragment"

        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }
}