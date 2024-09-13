package com.example.eventapp.util

import android.util.Log
import com.example.eventapp.BuildConfig
import com.example.eventapp.service.retrofit.RetrofitGeocodingInstance

class GetLocationData {
    private val locationApi = RetrofitGeocodingInstance.api
    suspend fun getLocationData(
        latitude: Double,
        longitude: Double
    ): Pair<String?, String?>? {
        val apiKey = BuildConfig.MAP_API_KEYS
        val latLng = "$latitude,$longitude"
        val response = locationApi.getCityFromLatLng(latLng, apiKey)
        if (response.isSuccessful) {
            val addressComponents = response.body()?.results?.firstOrNull()?.address_components
            val city =
                addressComponents?.find { it.types.contains(Constants.LOCATION) }?.long_name
            val countryCode =
                addressComponents?.find { it.types.contains(Constants.COUNTRY) }?.short_name
            return Pair(city, countryCode)
        } else {
            Log.e("HomeFragment", "Geocoding API request failed")
            return null
        }
    }
}