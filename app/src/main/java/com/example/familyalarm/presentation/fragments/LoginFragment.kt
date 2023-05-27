package com.example.familyalarm.presentation.fragments

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.allViews
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.chesire.lifecyklelog.LogLifecykle
import com.example.familyalarm.databinding.LoginFragmentBinding
import com.example.familyalarm.presentation.contract.navigator
import com.example.familyalarm.presentation.viewmodels.LoginVM
import com.example.familyalarm.utils.UiState.*
import com.example.familyalarm.utils.showErrorWithDisappearance
import com.example.familyalarm.utils.throwEx
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@LogLifecykle
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
            hideKeyboard()
            Log.d("LOGIN", "current auth id = ${FirebaseAuth.getInstance().currentUser?.uid?:"null"}")
            login()
        }

        binding.forgotPass.setOnClickListener {
            navigator().shouldLaunchFragment(
                ResetPasswordFragment.newInstance(),
                ResetPasswordFragment.NAME,
                true
            )
        }

        binding.textViewRegister.setOnClickListener {
            navigator().shouldLaunchFragment(
                RegisterFragment.newInstance(),
                RegisterFragment.NAME,
                true
            )
        }
    }


    private fun hideKeyboard() {
        val inputMethodManager =
            requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        if (requireActivity().currentFocus != null) {
            inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
        }

        // on below line hiding our keyboard.
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun login() {
        lifecycleScope.launch {
            val email = binding.textInputEditTextEmail.text.toString().trim()
            val password = binding.textInputEditTextPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                vm.login(email, password, requireContext())
            }
        }
    }

    private suspend fun observeVmState() {
        repeatOnLifecycle(Lifecycle.State.RESUMED) {
            vm.stateFlow.collect {
                Log.d("LoginFragment", "loginVmState: $it")
                when (it) {
                    Default -> {
                        binding.root.allViews.forEach {view-> view.isEnabled = true }
                        binding.progressBar.isVisible = false
                    }
                    Loading -> {
                        binding.progressBar.isVisible = true
                        binding.root.allViews.forEach {view-> view.isEnabled = false }
                    }
                    is Success -> {
                        navigator().shouldCloseFragment()
                        vm.checkIsParentOrChild()
                        vm.isParent.collectLatest {isParent->
                            when(isParent){
                                true ->  navigator().shouldLaunchFragment(
                                    ParentMainFragment.newInstance(),
                                    ParentMainFragment.NAME,
                                    false
                                )
                                false ->  navigator().shouldLaunchFragment(
                                    ChildMainFragment.newInstance(),
                                    ChildMainFragment.NAME,
                                    false
                                )
                                else -> {
                                    throwEx(observeVmState())
                                }
                            }
                        }
                    }
                    is Failure -> {
                        binding.progressBar.isVisible = false
                        binding.root.allViews.forEach { view-> view.isEnabled = true }
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


    companion object {

        const val NAME = "LoginFragment"

        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }
}