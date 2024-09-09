package com.example.eventapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.eventapp.databinding.FragmentSettingBinding
import com.example.eventapp.login.LoginScreen
import com.example.eventapp.viewmodel.SettingViewModel

class SettingFragment : Fragment() {

    private lateinit var binding: FragmentSettingBinding
    private val viewModel: SettingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.toastMessage.observe(viewLifecycleOwner, Observer { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            if (message == "Logged out successfully" || message == "Account deleted successfully") {
                navigateToLogin()
            }
        })

        binding.textOut.setOnClickListener {
            viewModel.logout()
        }

        binding.textDelete.setOnClickListener {
            viewModel.deleteUser()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(requireContext(), LoginScreen::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}