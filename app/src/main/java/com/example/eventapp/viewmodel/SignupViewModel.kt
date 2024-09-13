package com.example.eventapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.eventapp.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
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
        if (validateInput(email, password, displayName)) {
            registerUser(email, password, displayName)
        } else {
            postSignupError("Please enter all details")
        }
    }


    private fun validateInput(email: String, password: String, displayName: String): Boolean {
        return email.isNotEmpty() && password.isNotEmpty() && displayName.isNotEmpty()
    }

    // Register user with Firebase
    private fun registerUser(email: String, password: String, displayName: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            auth.currentUser?.let { user ->
                                updateUserProfile(user, displayName)
                            }
                        } else {
                            postSignupError(Constants.CREATE_USER_ERROR)
                        }
                    }
            } catch (e: Exception) {
                postSignupError(e.message ?: Constants.UNKNOWN_ERROR)
            }
        }
    }

    private fun updateUserProfile(user: FirebaseUser, displayName: String) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(displayName)
            .build()

        user.updateProfile(profileUpdates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _signupStatus.postValue(true)
            } else {
                postSignupError(Constants.FAILED_UPDATE_PROFILE)
            }
        }
    }

    private fun postSignupError(message: String) {
        _errorMessage.postValue(message)
        _signupStatus.postValue(false)
    }
}