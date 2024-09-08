package com.example.eventapp.login

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.example.eventapp.R
import com.example.eventapp.databinding.ActivityLoginScreenBinding
import com.example.eventapp.extension.openFragment

class LoginScreen : AppCompatActivity() {
    private lateinit var binding: ActivityLoginScreenBinding
    private lateinit var lottieAnimationView: LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lottieAnimationView = findViewById(R.id.lottie_animation_view)
        lottieAnimationView.visibility = View.VISIBLE
        lottieAnimationView.playAnimation()

        binding.buttonLogin.setOnClickListener {
            openFragment(SignInFragment(), R.id.fragment_container)
        }

        binding.buttonSignup.setOnClickListener {
            openFragment(SignupFragment(), R.id.fragment_container)
        }
    }
}