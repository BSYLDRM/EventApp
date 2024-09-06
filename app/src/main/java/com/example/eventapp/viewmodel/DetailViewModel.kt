package com.example.eventapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventapp.service.retrofit.RetrofitInstance
import com.example.eventapp.service.dataclass.Event
import kotlinx.coroutines.launch

class DetailViewModel : ViewModel() {

    private val _eventDetails = MutableLiveData<Event>()
    val eventDetails: LiveData<Event> get() = _eventDetails

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun fetchEventDetails(eventId: String) {
        _loading.value = true
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getEventDetails(eventId)
                _eventDetails.value = response
                _loading.value = false
            } catch (e: Exception) {
                _error.value = e.message
                _loading.value = false
            }
        }
    }
}
