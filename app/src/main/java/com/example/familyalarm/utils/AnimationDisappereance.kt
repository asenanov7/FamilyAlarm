package com.example.familyalarm.utils

import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.TextView


fun showErrorWithDisappearance(textView: TextView,textError:String, duration: Long) {
    val animation: Animation = AlphaAnimation(1.0f, 0.0f)
    animation.duration = duration // Устанавливаем длительность анимации в миллисекундах

    animation.setAnimationListener(object : Animation.AnimationListener{
        override fun onAnimationStart(p0: Animation?) {
            textView.text = textError
            textView.visibility = View.VISIBLE
        }

        override fun onAnimationEnd(p0: Animation?) {
            textView.text = ""
            textView.visibility = View.INVISIBLE
        }

        override fun onAnimationRepeat(p0: Animation?) {
            TODO("Not yet implemented")
        }
    })

    textView.startAnimation(animation)
}