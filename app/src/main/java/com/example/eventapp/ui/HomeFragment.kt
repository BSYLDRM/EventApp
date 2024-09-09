package com.example.eventapp.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.eventapp.adapter.EventsAdapter
import com.example.eventapp.databinding.FragmentHomeBinding
import com.example.eventapp.util.GetLocationData
import com.example.eventapp.util.LocationHelper
import com.example.eventapp.util.PermissionUtil
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

        binding.editTextName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchEventsByName(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
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
                    Log.d("HomeFragment", "No events found.")
                    lottieAnimationView.visibility = View.VISIBLE
                    recyclerViewHomeEvent.visibility = View.GONE
                }
            }
        }
    }
}
