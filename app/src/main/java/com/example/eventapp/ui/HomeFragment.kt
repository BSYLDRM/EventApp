package com.example.eventapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.eventapp.adapter.EventsAdapter
import com.example.eventapp.databinding.FragmentHomeBinding
import com.example.eventapp.service.retrofit.RetrofitGeocodingInstance
import com.example.eventapp.viewmodel.HomeViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getLastKnownLocation()
            } else {
                Log.d("HomeFragment", "Location permission denied")
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission()
            return
        }

        requestPermission()
        observeEvents()
    }

    private fun requestPermission() {
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // İzin verildi, konum alabilirsiniz
                getLastKnownLocation()
            } else {
                // İzin reddedildi
                println("İzin reddedildi.")
            }
        }

        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    @SuppressLint("SetTextI18n")
    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    Log.d("HomeFragment", "Location: ${location.latitude}, ${location.longitude}")
                    lifecycleScope.launch {
                        val locationData = getLocationData(location.latitude, location.longitude)
                        val city = locationData?.first
                        val countryCode = locationData?.second
                        if (city != null) {
                            Log.d("HomeFragment", "City: $city, Country: $countryCode")
                            viewModel.searchEventsByCity(city, countryCode)
                        } else {
                            Log.d("HomeFragment", "City is null")
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "Location not found", Toast.LENGTH_SHORT).show()
                    // Konum null ise burada nasıl işlem yapmak istediğinize karar verin
                }
            }
            .addOnFailureListener { exception ->
                Log.e("HomeFragment", "Error getting location", exception)
                // Hata durumunu yönetin
            }
    }

    private suspend fun getLocationData(
        latitude: Double,
        longitude: Double
    ): Pair<String?, String?>? {
        val apiKey = "AIzaSyDbUOCTzB4poidP9u3nu7UYWAI7Uoq77xg"
        val latLng = "$latitude,$longitude"
        val response = RetrofitGeocodingInstance.api.getCityFromLatLng(latLng, apiKey)
        if (response.isSuccessful) {
            val addressComponents = response.body()?.results?.firstOrNull()?.address_components
            val city =
                addressComponents?.find { it.types.contains("administrative_area_level_1") }?.long_name
            val countryCode = addressComponents?.find { it.types.contains("country") }?.short_name
            return Pair(city, countryCode)
        } else {
            Log.e("HomeFragment", "Geocoding API request failed")
            return null
        }
    }

    private fun observeEvents() {
        viewModel.events.observe(viewLifecycleOwner) { events ->
            if (events.isNotEmpty()) {
                val adapter = EventsAdapter(events)
                binding.recyclerViewHomeEvent.adapter = adapter
            } else {
                Log.d("HomeFragment", "No events found.")
            }
        }
    }
}