package com.example.eventapp.extension

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class LocationHelper(private val context: Context, private val callback: (latitude: Double, longitude: Double) -> Unit) {
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Konum izni yoksa işlemi burada yapabilirsiniz.
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    callback(location.latitude, location.longitude)
                } else {
                    Log.d("LocationHelper", "Location not found")
                    // Konum bulunamadığında yapılacak işlemleri burada yapabilirsiniz.
                }
            }
            .addOnFailureListener { exception ->
                Log.e("LocationHelper", "Error getting location", exception)
                // Hata durumunu burada işleyebilirsiniz.
            }
    }
}
