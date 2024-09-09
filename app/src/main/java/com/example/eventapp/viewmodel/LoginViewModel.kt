package com.example.eventapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _loginStatus = MutableLiveData<Boolean>()
    val loginStatus: LiveData<Boolean> get() = _loginStatus

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun loginUser(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            _loginStatus.postValue(true)
                        } else {
                            _errorMessage.postValue("Failed to log in")
                            _loginStatus.postValue(false)
                        }
                    }
                } catch (e: Exception) {
                    _errorMessage.postValue(e.message)
                    _loginStatus.postValue(false)
                }
            }
        } else {
            _errorMessage.postValue("Please enter email and password")
            _loginStatus.postValue(false)
        }
    }
}