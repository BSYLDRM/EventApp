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
import com.example.eventapp.databinding.FragmentSignupBinding
import com.example.eventapp.ui.HomeFragment
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

        binding.btnSignup.setOnClickListener {
            val email = binding.editSignupEmailAddress.text.toString()
            val password = binding.editSignupPassword.text.toString()
            signupViewModel.signUpUser(email, password)
        }

        signupViewModel.signupStatus.observe(viewLifecycleOwner, Observer { isSignedUp ->
            if (isSignedUp) {
                navigateToHome()
            }
        })

        signupViewModel.errorMessage.observe(viewLifecycleOwner, Observer { errorMessage ->
            errorMessage?.let {
                showToast(it)
            }
        })
    }
    private fun navigateToHome() {

        requireActivity().finish()

        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.putExtra(Constants.FRAGMENT_TO_OPEN,Constants.HOME_FRAGMENT)
        startActivity(intent)
    }


    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        showToast(Constants.TOAST_MESSAGE_ERROR)
    }
}