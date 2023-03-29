package com.example.familyalarm.presentation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
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

    private val fragmentManager by lazy { supportFragmentManager }


    override fun shouldCloseFragment() {
        fragmentManager.popBackStack()
    }

    override fun shouldLaunchFragment(fragment: Fragment, name: String, addToBackStack: Boolean) {
        launchFragment(fragment, name, addToBackStack)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if ( fragmentManager.fragments.isNotEmpty() ) {
            outState.putInt("Fragment", fragmentManager.fragments.last().id)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth.addAuthStateListener {
            if (auth.currentUser == null) {
                Log.d("SENANOV", "currentUser:==null ")
                //fragmentManager.popBackStackImmediate(LoginFragment.NAME, 0) //Удалить все фрагменты кроме Логина ТЕСТИРОВАТЬ
                launchFragment(LoginFragment.makeLoginFragment(), LoginFragment.NAME, false)
            } else {
                Log.d("SENANOV", "currentUser not null ")
            }
        }

      /*  if (savedInstanceState != null) {
            val fragmentId = savedInstanceState.getInt("Fragment")
            fragmentManager.findFragmentById(fragmentId)?.let {
                Log.d("SENANOV", "fragment $it ")
                launchFragment(it, it.toString(), false)
            }
        }else{
            launchFragment(MainFragment.makeMainFragment(),MainFragment.NAME,false)
        }*/
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