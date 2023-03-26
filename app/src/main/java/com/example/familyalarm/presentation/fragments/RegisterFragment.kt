package com.example.familyalarm.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.familyalarm.databinding.RegisterFragmentBinding

class RegisterFragment: Fragment(){

    private var _binding: RegisterFragmentBinding? = null
    private val binding: RegisterFragmentBinding
        get() = _binding ?: throw Exception("RegisterFragment == null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = RegisterFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object{
        fun launchRegisterFragment(): RegisterFragment {
            return RegisterFragment()
        }
    }
}