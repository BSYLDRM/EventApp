package com.example.eventapp.ui

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.provider.Settings
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.eventapp.R
import com.example.eventapp.adapter.EventsAdapter
import com.example.eventapp.databinding.FragmentHomeBinding
import com.example.eventapp.util.GetLocationData
import com.example.eventapp.util.LocationHelper
import com.example.eventapp.util.PermissionUtil
import com.example.eventapp.viewmodel.FavoriteViewModel
import com.example.eventapp.viewmodel.HomeViewModel
import com.google.android.material.snackbar.Snackbar
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
                showLocationPermissionDeniedSnackbar()
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
        checkLocationServicesEnabled()

        binding.lottieAnimationView.visibility = View.VISIBLE
        binding.recyclerViewHomeEvent.visibility = View.GONE

        binding.editTextName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchEventsByName(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun checkLocationServicesEnabled() {
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isGpsEnabled && !isNetworkEnabled) {
            showLocationSettingsSnackbar()
        }
    }

    private fun showLocationSettingsSnackbar() {
        Snackbar.make(binding.root,getString(R.string.location_services_disabled) , Snackbar.LENGTH_INDEFINITE)
        .setAction(getString(R.string.open_settings)) {
                openLocationSettings()
            }
            .show()
    }

    private fun openLocationSettings() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }


    private fun showLocationPermissionDeniedSnackbar() {
        Snackbar.make(binding.root,getString(R.string.location_permission_denied), Snackbar.LENGTH_LONG)
            .setAction(getString(R.string.grant_permission)) {
                checkLocationPermission()
            }
            .show()
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
        PermissionUtil.checkAndRequestLocationPermission(
            requireContext(),
            requestPermissionLauncher
        ) {
            locationHelper.getLastKnownLocation()
        }
    }

    private fun observeViewModel() {
        viewModel.events.observe(viewLifecycleOwner) { events ->
            with(binding) {
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
                    recyclerViewHomeEvent.adapter = adapter
                    recyclerViewHomeEvent.apply {
                        setAlpha(true)
                        set3DItem(true)
                    }
                    lottieAnimationView.visibility = View.GONE
                    recyclerViewHomeEvent.visibility = View.VISIBLE
                } else {
                    lottieAnimationView.visibility = View.VISIBLE
                    recyclerViewHomeEvent.visibility = View.GONE
                }
            }
        }
    }
}