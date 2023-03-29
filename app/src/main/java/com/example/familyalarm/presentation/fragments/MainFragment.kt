package com.example.familyalarm.presentation.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.familyalarm.databinding.MainFragmentBinding
import com.example.familyalarm.presentation.Navigation

class MainFragment: Fragment() {

    private var _binding: MainFragmentBinding? = null
    private val binding: MainFragmentBinding
        get() = _binding ?: throw Exception("MainFragment == null")


    private lateinit var navigation:Navigation
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Navigation) {
            navigation = context
        } else throw Exception(
            "If Activity use MainFragment, activity should implement Navigation"
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        Log.d("MainFragment", "onCreateView: MainFragment")
        _binding = MainFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        Log.d("MainFragment", "onDestroyView: MainFragment")
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainFragment", "onDestroy: MainFragment")
    }

    companion object{
        const val NAME: String = "MainFragment"

        fun newInstance():MainFragment {
            return MainFragment()
        }
    }
}