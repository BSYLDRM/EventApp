package com.example.eventapp.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat

class PermissionUtil {

    companion object {
        fun checkAndRequestLocationPermission(
            context: Context,
            requestPermissionLauncher: ActivityResultLauncher<String>,
            onPermissionGranted: () -> Unit
        ) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            } else {
                onPermissionGranted()
            }
        }
    }
}