package com.example.eventapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventapp.util.Constants.EVENT_ID
import com.example.eventapp.util.Constants.FAVORITES_COLLECTION
import com.example.eventapp.util.Constants.USERS_COLLECTION
import com.example.eventapp.service.retrofit.RetrofitInstance
import com.example.eventapp.service.dataclass.Event
import com.example.eventapp.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class FavoriteViewModel : ViewModel(){

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

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
            } catch (_: Exception) {

            }
        }
    }

    fun isFavorite(eventId: String, callback: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val favoriteRef = firestore.collection(USERS_COLLECTION).document(userId)
            .collection(FAVORITES_COLLECTION).document(eventId)

        favoriteRef.get().addOnSuccessListener { document ->
            callback(document.exists())
        }
    }

    fun toggleFavorite(event: Event, callback: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val favoriteRef = firestore.collection(USERS_COLLECTION).document(userId)
            .collection(FAVORITES_COLLECTION).document(event.id)

        favoriteRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                favoriteRef.delete().addOnSuccessListener {
                    callback(false)
                    fetchFavoritesFromFirebase()
                }
            } else {
                favoriteRef.set(mapOf(EVENT_ID to event.id)).addOnSuccessListener {
                    callback(true)
                    fetchFavoritesFromFirebase()
                }
            }
        }
    }

  fun fetchFavoritesFromFirebase() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection(Constants.USERS_COLLECTION)
            .document(userId)
            .collection(Constants.FAVORITES_COLLECTION)
            .get()
            .addOnSuccessListener { documents ->
                val favoriteIds = documents.map { it.id }
                fetchFavoriteEvents(favoriteIds)
            }
    }}