package com.example.eventapp.login

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.eventapp.MainActivity
import com.example.eventapp.databinding.FragmentSignInBinding
import com.example.eventapp.util.Constants
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

        with(binding) {
            buttonLogin.setOnClickListener {
                val email = editTextTextEmailAddress.text.toString()
                val password = editTextTextPassword.text.toString()
                loginViewModel.loginUser(email, password)
            }
        }

        loginViewModel.loginStatus.observe(viewLifecycleOwner) { isLoggedIn ->
            if (isLoggedIn) navigateToHome()
        }

        loginViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let { showToast(it) }
        }
    }

    private fun navigateToHome() {
        requireActivity().finish()
        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            putExtra(Constants.FRAGMENT_TO_OPEN, Constants.HOME_FRAGMENT)
        }
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}