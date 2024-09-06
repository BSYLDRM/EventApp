package com.example.eventapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventapp.service.retrofit.RetrofitInstance
import com.example.eventapp.service.dataclass.Event
import kotlinx.coroutines.launch

class FavoriteViewModel : ViewModel(){
    private val _favoriteEvents = MutableLiveData<List<Event>>()
    val favoriteEvents: LiveData<List<Event>> get() = _favoriteEvents
    private val eventApiService = RetrofitInstance.api


    fun fetchFavoriteEvents(favoriteIds: List<String>) {
        viewModelScope.launch {
            try {
                val events = favoriteIds.map { id ->
                    eventApiService.getEventDetails(id)
                }
                _favoriteEvents.value = events
            } catch (e: Exception) {

            }
        }
    }

}