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
import com.example.eventapp.adapter.EventsAdapter
import com.example.eventapp.databinding.FragmentHomeBinding
import com.example.eventapp.extension.GetLocationData
import com.example.eventapp.extension.LocationHelper
import com.example.eventapp.viewmodel.FavoriteViewModel
import com.example.eventapp.viewmodel.HomeViewModel
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private val viewModel: HomeViewModel by viewModels()
    private val favoriteViewModel: FavoriteViewModel by viewModels()

    private lateinit var locationHelper: LocationHelper
    private val getLocationData = GetLocationData()

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                locationHelper.getLastKnownLocation()
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

        setupLocationHelper()
        checkLocationPermission()
        observeViewModel()

        binding.lottieAnimationView.visibility = View.VISIBLE
        binding.recyclerViewHomeEvent.visibility = View.GONE
    }

    private fun setupLocationHelper() {
        locationHelper = LocationHelper(requireContext()) { latitude, longitude ->
            lifecycleScope.launch {
                val locationData = getLocationData.getLocationData(latitude, longitude)
                val city = locationData?.first
                val countryCode = locationData?.second
                if (city != null) {
                    Log.d("HomeFragment", "City: $city, Country: $countryCode")
                    binding.textCity.text = city
                    viewModel.searchEventsByCity(city, countryCode)
                } else {
                    Log.d("HomeFragment", "City is null")
                }
            }
        }
    }

    private fun checkLocationPermission() {
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

    private fun observeViewModel() {
        viewModel.events.observe(viewLifecycleOwner) { events ->
            if (events.isNotEmpty()) {
                val adapter = EventsAdapter(
                    eventsList = events,
                    isFavorite = { eventId, callback ->
                        favoriteViewModel.isFavorite(eventId, callback)
                    },
                    toggleFavorite = { event, callback ->
                        favoriteViewModel.toggleFavorite(event, callback)
                    }
                )
                binding.recyclerViewHomeEvent.adapter = adapter
                binding.recyclerViewHomeEvent.apply {
                    setAlpha(true)
                    set3DItem(true)
                }
                binding.lottieAnimationView.visibility = View.GONE
                binding.recyclerViewHomeEvent.visibility = View.VISIBLE
            } else {
                Log.d("HomeFragment", "No events found.")
                binding.lottieAnimationView.visibility = View.VISIBLE
                binding.recyclerViewHomeEvent.visibility = View.GONE
            }
        }
    }
}
