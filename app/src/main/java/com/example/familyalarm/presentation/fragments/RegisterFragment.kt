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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.familyalarm.databinding.RegisterFragmentBinding
import com.example.familyalarm.presentation.contract.navigator
import com.example.familyalarm.presentation.viewmodels.RegisterVM
import com.example.familyalarm.utils.UiState
import com.example.familyalarm.utils.UiState.*
import com.example.familyalarm.utils.getErrorMessageFromFirebaseErrorCode
import com.example.familyalarm.utils.showErrorWithDisappearance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

    private var _binding: RegisterFragmentBinding? = null
    private val binding: RegisterFragmentBinding
        get() = _binding ?: throw Exception("RegisterFragment == null")

    private val vm by lazy { ViewModelProvider(this)[RegisterVM::class.java] }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        Log.d("RegisterFragment", "onCreateView: RegisterFragment $this")
        _binding = RegisterFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            observeVmState()
        }

        binding.buttonReg.setOnClickListener {
            hideKeyboard()
            registerAndCreateUser()
        }

    }

    private suspend fun observeVmState() {
        repeatOnLifecycle(Lifecycle.State.RESUMED) {
            vm.stateFlow.collect {
                Log.d("RegisterFragment", "regState: $it ")
                when (it) {
                    Default -> {
                        binding.buttonReg.isEnabled = true
                        binding.progressBar.isVisible = false
                    }
                    Loading -> {
                        binding.buttonReg.isEnabled = false
                        binding.progressBar.isVisible = true
                        Log.d("RegisterFragment", "binding.progressBar.isVisible = true")
                    }
                    is Success -> {
                        navigator().shouldCloseFragment()
                        navigator().shouldLaunchFragment(
                            MainFragment.newInstance(), MainFragment.NAME, false
                        )
                    }
                    is Failure -> {
                        binding.buttonReg.isEnabled = true
                        binding.progressBar.isVisible = false
                        showErrorWithDisappearance(
                            binding.textviewErrors, it.exceptionMessage, 5000
                        )
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        Log.d("RegisterFragment", "onDestroyView: RegisterFragment $this")
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        Log.d("RegisterFragment", "onDestroy: RegisterFragment $this")
        super.onDestroy()
    }

    private fun registerAndCreateUser() {
        val name = binding.textInputEditTextName.text.toString().trim()
        val email = binding.textInputEditTextEmail.text.toString().trim()
        val password = binding.textInputEditTextPassword.text.toString().trim()
        val isParent = binding.radioButtonParent.isChecked
        val isChildren = binding.radioButtonChild.isChecked

        if (!isChildren && !isParent){
            showErrorWithDisappearance(binding.textviewErrors,"Пожалуйста выберите роль", 5000)
            return
        }

        if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
            lifecycleScope.launch {
                if (isParent) {
                    vm.registerAndCreateParent(
                        name = name,
                        email = email,
                        password = password,
                        context = requireContext()
                    )
                }else{
                    vm.registerAndCreateChild(
                        name = name,
                        email = email,
                        password = password,
                        context = requireContext()
                    )
                }
            }
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (requireActivity().currentFocus != null) {
            inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
        }

        // on below line hiding our keyboard.
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    companion object {
        const val NAME: String = "RegisterFragment"

        fun newInstance(): RegisterFragment {
            return RegisterFragment()
        }
    }
}