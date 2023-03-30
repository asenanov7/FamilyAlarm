package com.example.familyalarm.presentation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import com.example.familyalarm.R
import com.example.familyalarm.databinding.ActivityMainBinding
import com.example.familyalarm.presentation.fragments.LoginFragment
import com.example.familyalarm.presentation.fragments.MainFragment
import com.example.familyalarm.presentation.fragments.RegisterFragment
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity(), Navigation {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val auth by lazy {
        FirebaseAuth.getInstance()
    }

    private val fragmentManager by lazy { supportFragmentManager }


    override fun shouldCloseFragment() {
        fragmentManager.popBackStack()  //Удаляет из стека один фрагмент
    }

    override fun shouldLaunchFragment(fragment: Fragment, name: String, addToBackStack: Boolean) {
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


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (fragmentManager.fragments.isNotEmpty()) {
            outState.putInt("Fragment", fragmentManager.fragments.last().id)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        /*    auth.addAuthStateListener {
                if (auth.currentUser == null) {
                    Log.d("SENANOV", "currentUser:==null : launch LoginFragment ")
                    //fragmentManager.popBackStackImmediate(LoginFragment.NAME, 0) //Удалить все фрагменты кроме Логина ТЕСТИРОВАТЬ
                    shouldLaunchFragment(LoginFragment.newInstance(), LoginFragment.NAME, false)
                } else {*/

        //Убрать, оно для тестов тут
        if (auth.currentUser!=null){
            auth.signOut()
        } //

        if (auth.currentUser == null) {
            Log.d("SENANOV", "auth.currentUser == null ")
            if (savedInstanceState==null) {
                shouldLaunchFragment(LoginFragment.newInstance(), LoginFragment.NAME, false)
            }
        } else {
            if (savedInstanceState == null) {
                shouldLaunchFragment(MainFragment.newInstance(), MainFragment.NAME, false)
                Log.d(
                    "SENANOV",
                    "currentUser not null - savedInstanceState == null : laucnh MainFragment"
                )
            } else {
                //Может вообще не нужно (Кажись андроид сам восстанавливает последний фрагмент)
                val fragmentId = savedInstanceState.getInt("Fragment")
                fragmentManager.findFragmentById(fragmentId)?.let {
                    Log.d(
                        "SENANOV",
                        "currentUser not null - savedInstanceState !=null : launch OLD fragment $it "
                    )
                    shouldLaunchFragment(it, it.toString(), true)
                }
            }
        }
    }
}




