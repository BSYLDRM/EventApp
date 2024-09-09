package com.example.eventapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.eventapp.databinding.ActivityMainBinding
import com.example.eventapp.login.LoginScreen
import com.example.eventapp.ui.FavoriteFragment
import com.example.eventapp.ui.HomeFragment
import com.example.eventapp.ui.MapsFragment
import com.example.eventapp.ui.SearchFragment
import com.example.eventapp.ui.SettingFragment
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (FirebaseAuth.getInstance().currentUser == null) {
            val intent = Intent(this, LoginScreen::class.java)
            startActivity(intent)
            finish()
        }

        binding.navigationBarView.setOnItemSelectedListener { pos ->
            when (pos) {
                0 -> {
                    openFragment(R.id.mainFragmentContainer, HomeFragment())
                }

                1 -> {
                    openFragment(R.id.mainFragmentContainer, MapsFragment())
                }

                2 -> {
                    openFragment(R.id.mainFragmentContainer, SearchFragment())
                }

                3 -> {
                    openFragment(R.id.mainFragmentContainer, FavoriteFragment())
                }

                4 -> {
                    openFragment(R.id.mainFragmentContainer, SettingFragment())
                }

                else -> {
                    openFragment(R.id.mainFragmentContainer, HomeFragment())
                }
            }
        }
    }

    private fun openFragment(container: Int, fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(container, fragment)
            .addToBackStack(null)
            .commit()
    }
}