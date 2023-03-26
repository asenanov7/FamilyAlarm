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
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val binding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth.signOut()
        auth.addAuthStateListener {
            val fragment = if (it.currentUser != null){
                MainFragment.makeMainFragment()
            }else{
                LoginFragment.makeLoginFragment()
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView, fragment)
                .commit()
        }

    }
}