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
import com.example.eventapp.databinding.FragmentSignupBinding
import com.example.eventapp.util.Constants
import com.example.eventapp.viewmodel.SignupViewModel

class SignupFragment : Fragment() {

    private lateinit var binding: FragmentSignupBinding
    private val signupViewModel: SignupViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnSignup.setOnClickListener {
                val email = editSignupEmailAddress.text.toString()
                val password = editSignupPassword.text.toString()
                val displayName = editTextName.text.toString()

                signupViewModel.signUpUser(email, password, displayName)
            }
        }

        signupViewModel.signupStatus.observe(viewLifecycleOwner) { isSignedUp ->
            if (isSignedUp) navigateToHome()
        }

        signupViewModel.errorMessage.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let { showToast(it) }
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