package com.example.eventapp.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.eventapp.MainActivity
import com.example.eventapp.databinding.ActivityLoginScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginScreen : AppCompatActivity() {
    private lateinit var binding: ActivityLoginScreenBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        if (auth.currentUser != null) {
            navigateToHomeFragment()
        }

        binding.buttonLogin.setOnClickListener {
            loginUser()
        }

        binding.buttonSignup.setOnClickListener {
            navigateToSignUpActivity()
        }
    }

    private fun loginUser() {
        val email = binding.editTextTextEmailAddress.text.toString()
        val password = binding.editTextTextPassword.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        navigateToHomeFragment()
                    } else {
                        Toast.makeText(this, "Kullanıcı adı veya şifre yanlış", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        } else {
            Toast.makeText(this, "Lütfen e-posta ve şifre girin", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToHomeFragment() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToSignUpActivity() {
        val intent = Intent(this, SignupScreen::class.java)
        startActivity(intent)
    }

}