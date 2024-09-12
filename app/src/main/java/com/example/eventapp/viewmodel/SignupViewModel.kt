package com.example.eventapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SignupViewModel : ViewModel() {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val _signupStatus = MutableLiveData<Boolean>()
    val signupStatus: LiveData<Boolean> get() = _signupStatus

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun signUpUser(email: String, password: String, displayName: String) {
        if (email.isNotEmpty() && password.isNotEmpty() && displayName.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                user?.let {
                                    val profileUpdates = UserProfileChangeRequest.Builder()
                                        .setDisplayName(displayName)
                                        .build()

                                    it.updateProfile(profileUpdates).addOnCompleteListener { updateTask ->
                                        if (updateTask.isSuccessful) {
                                            _signupStatus.postValue(true)
                                        } else {
                                            _errorMessage.postValue("Failed to update profile")
                                            _signupStatus.postValue(false)
                                        }
                                    }
                                }
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
            _errorMessage.postValue("Please enter all details")
            _signupStatus.postValue(false)
        }
    }
}