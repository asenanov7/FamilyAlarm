package com.example.familyalarm.presentation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.familyalarm.R
import com.example.familyalarm.data.impl_repositories.AuthRepositoryImpl
import com.example.familyalarm.databinding.ActivityMainBinding
import com.example.familyalarm.presentation.fragments.LoginFragment
import com.example.familyalarm.presentation.fragments.MainFragment
import com.example.familyalarm.presentation.fragments.ResetPasswordFragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), Navigation {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private val fragmentManager = supportFragmentManager


    override fun shouldCloseFragment() {
        fragmentManager.popBackStack()
    }

    override fun shouldLaunchFragment(fragment: Fragment, name: String, addToBackStack: Boolean) {
        launchFragment(fragment, name, addToBackStack)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        isLoggedListener()

    }

    private fun isLoggedListener() {
        auth.addAuthStateListener {
            if (it.currentUser == null) {
                launchFragment(
                    fragment = LoginFragment.makeLoginFragment(),
                    name = LoginFragment.NAME,
                    addToBackStack = false
                )
            } else {
                launchFragment(
                    fragment = MainFragment.makeMainFragment(),
                    name = MainFragment.NAME,
                    addToBackStack = false
                )
            }

        }
    }

    private fun launchFragment(fragment: Fragment, name: String, addToBackStack: Boolean) {
        if (addToBackStack) {
            fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .addToBackStack(name)
                .commit()
        } else {
            fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .commit()
        }
    }



}