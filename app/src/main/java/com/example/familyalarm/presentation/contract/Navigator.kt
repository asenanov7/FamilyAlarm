package com.example.familyalarm.presentation.contract

import androidx.fragment.app.Fragment

fun Fragment.navigator(): Navigator{
    return requireActivity() as Navigator
}

interface Navigator {

    fun shouldCloseFragment()

    fun shouldLaunchFragment(fragment: Fragment, name: String, addToBackStack: Boolean)


}