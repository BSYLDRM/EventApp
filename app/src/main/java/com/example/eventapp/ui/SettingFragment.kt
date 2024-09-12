package com.example.eventapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.eventapp.databinding.FragmentSettingBinding
import com.example.eventapp.extension.showToast
import com.example.eventapp.login.LoginScreenActivity
import com.example.eventapp.util.Constants.ACC_DELETED
import com.example.eventapp.util.Constants.LOGGED_OUT
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

        viewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            requireContext().showToast(message)
            if (message == LOGGED_OUT || message == ACC_DELETED) {
                navigateToLogin()
            }
        }

        with(binding) {
            textOut.setOnClickListener {
                viewModel.logout()
            }

            textDelete.setOnClickListener {
                viewModel.deleteUser()
            }
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(requireContext(), LoginScreenActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}
