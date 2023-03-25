package com.example.familyalarm.presentation

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.familyalarm.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private val binding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }

    private  val auth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

     /*   auth.createUserWithEmailAndPassword("test@gmail.com", "123456")
            .addOnSuccessListener { Log.d("ARSEN", "onCreate: success") }
            .addOnFailureListener { Log.d("ARSEN", "Fail $it")}*/

        auth.sendPasswordResetEmail("test@gmail.com")
            .addOnSuccessListener { Log.d("ARSEN", "onCreate: success") }
            .addOnFailureListener { Log.d("ARSEN", "Fail $it")}
    }
}