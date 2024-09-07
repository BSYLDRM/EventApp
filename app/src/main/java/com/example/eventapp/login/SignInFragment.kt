package com.example.eventapp.login

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.eventapp.MainActivity
import com.example.eventapp.R
import com.example.eventapp.databinding.FragmentSignInBinding
import com.example.eventapp.ui.HomeFragment
import com.example.eventapp.viewmodel.LoginViewModel

class SignInFragment : Fragment() {
    private lateinit var binding: FragmentSignInBinding
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonLogin.setOnClickListener {
            val email = binding.editTextTextEmailAddress.text.toString()
            val password = binding.editTextTextPassword.text.toString()
            loginViewModel.loginUser(email, password)
        }

        loginViewModel.loginStatus.observe(viewLifecycleOwner, Observer { isLoggedIn ->
            if (isLoggedIn) {
                navigateToHome()
            }
        })

        loginViewModel.errorMessage.observe(viewLifecycleOwner, Observer { errorMessage ->
            errorMessage?.let {
                showToast(it)
            }
        })
    }
    private fun navigateToHome() {
        // Mevcut aktiviteyi bitir
        requireActivity().finish()

        // MainActivity'yi başlat ve HomeFragment'ı yerleştir
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.putExtra("fragment_to_open", "home") // Gerekirse veri ekleyin
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}