package com.example.eventapp.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
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
import com.example.eventapp.extension.showToast
import com.example.eventapp.extension.visibilityGone
import com.example.eventapp.extension.visibilityVisible
import com.example.eventapp.util.Constants
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
                showLocationPermissionDeniedSnackBar()
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

        binding.lottieAnimationView.visibilityVisible()
        binding.lottieNoData.visibilityGone()
        binding.recyclerViewHomeEvent.visibilityGone()
    }

    override fun onResume() {
        super.onResume()
        checkLocationServicesEnabled()
        locationHelper.getLastKnownLocation()
    }

    private fun checkLocationServicesEnabled() {
        val locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isGpsEnabled && !isNetworkEnabled) {
            showLocationSettingsSnackBar()
        }
    }

    private fun showLocationSettingsSnackBar() {
        Snackbar.make(
            binding.root,
            getString(R.string.location_services_disabled),
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction(getString(R.string.open_settings)) {
                openLocationSettings()
            }
            .show()
    }

    private fun openLocationSettings() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    private fun showLocationPermissionDeniedSnackBar() {
        Snackbar.make(
            binding.root,
            getString(R.string.location_permission_denied),
            Snackbar.LENGTH_LONG
        )
            .setAction(getString(R.string.grant_permission)) {
                openAppSettings()
            }
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", requireContext().packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun setupLocationHelper() {
        locationHelper = LocationHelper(requireContext()) { latitude, longitude ->
            lifecycleScope.launch {
                val locationData = getLocationData.getLocationData(latitude, longitude)
                val city = locationData?.first
                val countryCode = locationData?.second
                if (city != null) {
                    viewModel.searchEventsByCity(city, countryCode)
                } else {
                    requireContext().showToast(Constants.CITY_NULL)
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

    @SuppressLint("SetTextI18n")
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

                    lottieAnimationView.visibilityGone()
                    lottieNoData.visibilityGone()
                    recyclerViewHomeEvent.visibilityVisible()
                } else {
                    lottieAnimationView.visibilityGone()
                    lottieNoData.visibilityVisible()
                    recyclerViewHomeEvent.visibilityGone()
                }
            }
        }

        viewModel.userName.observe(viewLifecycleOwner) { name ->
            val formattedName = name.replaceFirstChar { it.uppercase() }
            binding.textName.text = getString(R.string.hi_home, formattedName)
        }
    }
}