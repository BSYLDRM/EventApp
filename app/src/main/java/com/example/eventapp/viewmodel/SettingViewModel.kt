package com.example.eventapp.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class SettingViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> get() = _toastMessage

    fun logout() {
        auth.signOut()
        _toastMessage.value = "Logged out successfully"
    }

    fun deleteUser() {
        val user = auth.currentUser
        user?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _toastMessage.value = "Account deleted successfully"
            } else {
                task.exception?.let {
                    _toastMessage.value = "Account deletion failed: ${it.message}"
                }
            }
        }
    }
}
