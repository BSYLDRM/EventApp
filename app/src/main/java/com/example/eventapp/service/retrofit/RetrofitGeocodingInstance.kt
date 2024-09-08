package com.example.eventapp.service.retrofit

import com.example.eventapp.BuildConfig
import com.example.eventapp.service.Service
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitGeocodingInstance {

    val api: Service by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.MAP_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Service::class.java)
    }
}