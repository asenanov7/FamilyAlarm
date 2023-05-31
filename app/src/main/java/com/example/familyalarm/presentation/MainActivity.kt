package com.example.familyalarm.presentation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.familyalarm.R
import com.example.familyalarm.data.impl_repositories.ChildRepositoryImpl
import com.example.familyalarm.data.impl_repositories.GeneralRepositoryImpl
import com.example.familyalarm.data.impl_repositories.ParentRepositoryImpl
import com.example.familyalarm.databinding.ActivityMainBinding
import com.example.familyalarm.domain.entities.User
import com.example.familyalarm.domain.entities.UserParent
import com.example.familyalarm.domain.repositories.GeneralRepository
import com.example.familyalarm.presentation.contract.Navigator
import com.example.familyalarm.presentation.fragments.ChildMainFragment
import com.example.familyalarm.presentation.fragments.LoginFragment
import com.example.familyalarm.presentation.fragments.ParentMainFragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), Navigator {

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

        if (auth.currentUser == null) {
            Log.d("SENANOV", "auth.currentUser == null ")
            if (savedInstanceState==null) {
                shouldLaunchFragment(LoginFragment.newInstance(), LoginFragment.NAME, false)
            }
        } else {
            if (savedInstanceState == null) {
                lifecycleScope.launch {
                    var user: User? =null
                    lifecycleScope.launch {
                        user = GeneralRepositoryImpl.create().getUserInfo(FirebaseAuth.getInstance().currentUser!!.uid)
                    }.join()

                    if (user is UserParent) {
                        shouldLaunchFragment(
                            ParentMainFragment.newInstance(),
                            ParentMainFragment.NAME,
                            false
                        )
                    }else{
                        shouldLaunchFragment(
                            ChildMainFragment.newInstance(),
                            ChildMainFragment.NAME,
                            false
                        )
                    }
                }
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

    override fun onDestroy() {
        super.onDestroy()
        Log.d("FIX_LISTENERS_BUG", "onDestroy")
        //GeneralRepositoryImpl.destroy()
        //ChildRepositoryImpl.destroy()
        //ParentRepositoryImpl.destroy()
    }
}




