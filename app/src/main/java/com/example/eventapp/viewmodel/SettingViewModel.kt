package com.example.eventapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.eventapp.util.Constants.ACC_DELETED
import com.example.eventapp.util.Constants.LOGGED_OUT
import com.google.firebase.auth.FirebaseAuth

class SettingViewModel : ViewModel() {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage

    fun logout() {
        auth.signOut()
        _toastMessage.value = LOGGED_OUT
    }

    fun deleteUser() {
        val user = auth.currentUser
        user?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _toastMessage.value = ACC_DELETED
            } else {
                task.exception?.let {
                    _toastMessage.value = "Account deletion failed: ${it.message}"
                }
            }
        }
    }
}
