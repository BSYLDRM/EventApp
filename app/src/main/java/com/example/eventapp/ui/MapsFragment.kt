package com.example.eventapp.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.eventapp.R
import com.example.eventapp.extension.GetLocationData
import com.example.eventapp.extension.LocationHelper
import com.example.eventapp.viewmodel.HomeViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MapsFragment : Fragment() {

    private lateinit var locationHelper: LocationHelper
    private val viewModel: HomeViewModel by viewModels()

    private val callback = OnMapReadyCallback { googleMap ->
        setupLocationHelper(googleMap)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                locationHelper.getLastKnownLocation()
            } else {
                Log.d("MapsFragment", "Location permission denied")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun setupLocationHelper(googleMap: GoogleMap) {
        locationHelper = LocationHelper(requireContext()) { latitude, longitude ->
            lifecycleScope.launch {
                val locationData = GetLocationData().getLocationData(latitude, longitude)
                val city = locationData?.first
                val countryCode = locationData?.second
                if (city != null) {
                    Log.d("MapsFragment", "City: $city, Country: $countryCode")
                    viewModel.searchEventsByCity(city, countryCode)
                } else {
                    Log.d("MapsFragment", "City is null")
                }
            }
            observeEvents(googleMap, latitude, longitude)
        }

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            locationHelper.getLastKnownLocation()
        }
    }

    private fun observeEvents(googleMap: GoogleMap, latitude: Double, longitude: Double) {
        viewModel.events.observe(viewLifecycleOwner) { events ->
            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.Main) {
                    googleMap.clear()
                    events.forEach { event ->
                        val venue = event.embedded.venues.firstOrNull()
                        val location = venue?.location?.let { loc ->
                            val latStr = loc.latitude
                            val lngStr = loc.longitude
                            val lat = latStr?.toDoubleOrNull()
                            val lng = lngStr?.toDoubleOrNull()
                            if (lat != null && lng != null) {
                                LatLng(lat, lng)
                            } else {
                                Log.w("MapsFragment", "Invalid location data for event: ${event.name}")
                                null
                            }
                        }

                        if (location != null) {
                            googleMap.addMarker(
                                MarkerOptions().position(location).title(event.name)
                            )
                        }
                    }
                    val userLocation = LatLng(latitude, longitude)
                    googleMap.addMarker(
                        MarkerOptions().position(userLocation).title("Current Location")
                    )
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12f))
                }
            }
        }
    }
}