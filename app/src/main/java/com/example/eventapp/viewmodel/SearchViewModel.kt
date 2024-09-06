package com.example.eventapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventapp.service.dataclass.Event
import com.example.eventapp.service.retrofit.RetrofitInstance
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    private val _searchResults = MutableLiveData<List<Event>>()
    val searchResults: LiveData<List<Event>> get() = _searchResults

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val apiService = RetrofitInstance.api

    fun searchEvents(query: String) {
        _loading.value = true
        viewModelScope.launch {
            try {
                val response = apiService.getEvents(
                    keyword = query,
                    page = 1
                )
                val events = response.embedded.events ?: emptyList()
                _searchResults.value = events
            } catch (e: Exception) {
                _error.value = "Arama sırasında bir hata oluştu: ${e.localizedMessage}"
            } finally {
                _loading.value = false
            }
        }
    }

    private fun isCityQuery(query: String): Boolean {
        // Burada şehirlere özel bir kontrol yapabilirsiniz, örneğin:
        // return query.matches(Regex("^[A-Za-z]+$")) // Basit bir şehir kontrolü
        return true // Şimdilik tüm sorguları şehir olarak kabul ediyoruz
    }
}