package com.example.eventapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventapp.service.dataclass.EventsResponse
import com.example.eventapp.service.retrofit.RetrofitInstance
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val _events = MutableLiveData<EventsResponse>()
    val events: LiveData<EventsResponse> get() = _events
    private val eventApiService = RetrofitInstance.api

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun searchEvents(city: String?) {
        viewModelScope.launch {
            try {
                val response = eventApiService.getEvents(city = city)
                _events.postValue(response)
            } catch (e: Exception) {
                _error.postValue(e.message)
            }
        }
    }
}
