package com.example.eventapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventapp.util.Constants.ENTER_EMAIL_PASSWORD
import com.example.eventapp.util.Constants.FAILED_LOGIN
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


class LoginViewModel : ViewModel() {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val _loginStatus = MutableLiveData<Boolean>()
    val loginStatus: LiveData<Boolean> get() = _loginStatus

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun loginUser(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            viewModelScope.launch {
                try {
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            _loginStatus.postValue(true)
                        } else {
                            _errorMessage.postValue(FAILED_LOGIN)
                            _loginStatus.postValue(false)
                        }
                    }
                } catch (e: Exception) {
                    _errorMessage.postValue(e.message)
                    _loginStatus.postValue(false)
                }
            }
        } else {
            _errorMessage.postValue(ENTER_EMAIL_PASSWORD)
            _loginStatus.postValue(false)
        }
    }
}
