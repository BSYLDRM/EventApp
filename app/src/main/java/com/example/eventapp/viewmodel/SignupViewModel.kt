package com.example.eventapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignupViewModel: ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _signupStatus = MutableLiveData<Boolean>()
    val signupStatus: LiveData<Boolean> get() = _signupStatus

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun signUpUser(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            _signupStatus.postValue(true)
                        } else {
                            _errorMessage.postValue("Failed to create user")
                            _signupStatus.postValue(false)
                        }
                    }
                } catch (e: Exception) {
                    _errorMessage.postValue(e.message)
                    _signupStatus.postValue(false)
                }
            }
        } else {
            _errorMessage.postValue("Please enter email and password")
            _signupStatus.postValue(false)
        }
    }
}