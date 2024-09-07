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

    // Hata durumları için LiveData
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    // Şehir ismine göre arama yapacak şekilde düzenlenmiş
    fun searchEvents(city: String?) {
        if ((city?.length ?: 0) < 3) {
            // Şehir ismi 3 karakterden küçükse arama yapılmaz
            _error.postValue("Şehir ismi en az 3 karakter olmalıdır.")
            return
        }

        // Coroutines ile API çağrısı yapıyoruz
        viewModelScope.launch {
            try {
                val response = eventApiService.getEvents(city = city)
                _events.postValue(response)
            } catch (e: Exception) {
                _error.postValue(e.message) // Hata mesajını LiveData'ya gönder
            }
        }
    }
}