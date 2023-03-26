package com.example.familyalarm.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.familyalarm.databinding.ResetPasswordFragmentBinding

class ResetPasswordFragment : Fragment() {

    private var _binding: ResetPasswordFragmentBinding? = null
    private val binding: ResetPasswordFragmentBinding
        get() = _binding ?: throw Exception("ResetPasswordFragment == null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = ResetPasswordFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object{
        fun launchResetPasswordFragment(): ResetPasswordFragment {
            return ResetPasswordFragment()
        }
    }
}