package com.example.familyalarm.presentation.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doBeforeTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.example.familyalarm.R
import com.example.familyalarm.databinding.LoginFragmentBinding
import com.example.familyalarm.utils.Utils
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private val auth by lazy { FirebaseAuth.getInstance() }

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

        setValidListener()

        binding.buttonSignUp.setOnClickListener {

            val email = binding.textInputEditTextEmail.text.toString().trim()
            val password = binding.textInputEditTextPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) { return@setOnClickListener }

            auth.signInWithEmailAndPassword(email, password)
                .addOnFailureListener {Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setValidListener() {
        val timerAndValidEmail = object : CountDownTimer(1000, 1000) {
            override fun onTick(p0: Long) {}
            override fun onFinish() {
                val email = binding.textInputEditTextEmail.text.toString().trim()
                if (Utils.isEmailValid(email) || email.isEmpty()) {
                    return
                } else {
                    binding.textInputLayoutEmail.error =
                        getString(R.string.error_please_enter_valid_email)
                    //Если нет, вывод ошибки
                }
            }
        }
        val timerAndValidPassword = object : CountDownTimer(1500, 1000) {
            override fun onTick(p0: Long) {}
            override fun onFinish() {
                val password = binding.textInputEditTextPassword.text.toString().trim()
                if (password.length > 7 || password.isEmpty()) {
                    return
                } else {
                    binding.textInputLayoutPassword.error = getString(R.string.error_short_pass)
                    //Если нет, вывод ошибки
                }
            }
        }

        with(binding) {
            textInputEditTextEmail.doOnTextChanged { text, start, before, count -> timerAndValidEmail.start() }
            textInputEditTextEmail.doBeforeTextChanged { text, start, count, after -> textInputLayoutEmail.error = null }
            textInputEditTextPassword.doOnTextChanged { text, start, before, count -> timerAndValidPassword.start() }
            textInputEditTextPassword.doBeforeTextChanged { text, start, count, after -> textInputLayoutPassword.error = null }
        }
    }

    private fun checkErrors(){
        if (binding.textInputLayoutEmail.error!=null||binding.textInputLayoutPassword.error!=null){
            binding.buttonSignUp.isEnabled = false
        }
    }

    companion object {
        fun makeLoginFragment(): LoginFragment {
            return LoginFragment()
        }
    }
}