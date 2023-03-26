package com.example.familyalarm.utils

import android.text.TextUtils

class Utils {
    companion object {
        fun isEmailValid(target: CharSequence): Boolean {
            return if (TextUtils.isEmpty(target)) {
                false
            } else {
                android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
            }
        }
    }
}