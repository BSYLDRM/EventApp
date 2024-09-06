package com.example.eventapp.service

import com.example.eventapp.service.dataclass.Event
import com.example.eventapp.service.dataclass.EventsResponse
import com.example.eventapp.service.dataclass.GeocodingResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Service {
    @GET(searchEvent)
    suspend fun getEvents(
        @Query("page") page: Int = 20,
        @Query("classificationName") classificationName: List<String>?=null,
        @Query("classificationId") classificationId:List<String>?=null,
        @Query("countryCode") countryCode: String? = null,
        @Query("city") city: String? = null,
        @Query("keyword") keyword: String? = null,
    ): EventsResponse


    @GET("events/{id}.json")
    suspend fun getEventDetails(
        @Path("id") eventId: String
    ): Event

    @GET("geocode/json")
    suspend fun getCityFromLatLng(
        @Query("latlng") latLng: String,
        @Query("key") apiKey: String
    ): Response<GeocodingResponse>

}
const val searchEvent = "events.json"



