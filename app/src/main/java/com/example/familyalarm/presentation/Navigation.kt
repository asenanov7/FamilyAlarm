package com.example.familyalarm.presentation

import androidx.fragment.app.Fragment

interface Navigation {

    fun shouldCloseFragment()

    fun shouldLaunchFragment(fragment: Fragment, name: String, addToBackStack: Boolean )


}