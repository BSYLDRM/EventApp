package com.example.eventapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventapp.service.dataclass.Event
import com.example.eventapp.service.retrofit.RetrofitInstance
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _events = MutableLiveData<List<Event>>()
    val events: LiveData<List<Event>> = _events
    private val eventApiService = RetrofitInstance.api

    fun searchEventsByCity(city: String?, countryCode: String?) {
        viewModelScope.launch {
            try {
                val response = eventApiService.getEvents(city = city)
                if (response.embedded.events.isEmpty()) {
                    searchEventsByCountry(countryCode)
                } else {
                    _events.postValue(response.embedded.events)
                }
            } catch (e: Exception) {
                _events.postValue(emptyList())
            }
        }
    }

    private fun searchEventsByCountry(countryCode: String?) {
        viewModelScope.launch {
            try {
                val response = eventApiService.getEvents(countryCode = countryCode)
                _events.postValue(response.embedded.events)
            } catch (e: Exception) {
                _events.postValue(emptyList())
            }
        }
    }
}