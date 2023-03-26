package com.example.familyalarm.presentation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.familyalarm.data.impl_repositories.AuthRepositoryImpl
import com.example.familyalarm.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val binding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val authRepositoryImpl = AuthRepositoryImpl()
        lifecycleScope.launch {
                authRepositoryImpl.login("test@gmail.com", "123456")
                    .collect{
                        Log.d("ARSEN", "State: $it")
                        }
            }
    }
}