package com.example.eventapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.eventapp.databinding.ActivityMainBinding
import com.example.eventapp.login.LoginScreen
import com.example.eventapp.ui.FavoriteFragment
import com.example.eventapp.ui.HomeFragment
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

        setupNavigationBar()
    }

    private fun setupNavigationBar() {
        binding.navigationBarView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    openFragment(R.id.mainFragmentContainer, HomeFragment())
                    true
                }

                R.id.nav_filter -> {
                    openFragment(R.id.mainFragmentContainer, SearchFragment())
                    true
                }

                R.id.nav_favorite -> {
                    openFragment(R.id.mainFragmentContainer, FavoriteFragment())
                    true
                }

                R.id.nav_settings -> {
                    openFragment(R.id.mainFragmentContainer, SettingFragment())
                    true
                }

                else -> false
            }
        }
    }

    private fun openFragment(container: Int, fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(container, fragment).commit()
    }
}