package com.example.eventapp.login

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.example.eventapp.R
import com.example.eventapp.databinding.ActivityLoginScreenBinding

class LoginScreen : AppCompatActivity() {
    private lateinit var binding: ActivityLoginScreenBinding
    private lateinit var lottieAnimationView: LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lottieAnimationView = findViewById(R.id.lottie_animation_view)

        // Animasyonu başlat
        lottieAnimationView.visibility = View.VISIBLE
        lottieAnimationView.playAnimation()

        binding.buttonLogin.setOnClickListener {
            replaceFragment(SignInFragment())
        }

        binding.buttonSignup.setOnClickListener {
            replaceFragment(SignupFragment())
        }
    }

    fun replaceFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)

        if (addToBackStack) {
            transaction.addToBackStack(null) // Geri dönülebilir bir yığın ekle
        }

        transaction.commit()
    }

}