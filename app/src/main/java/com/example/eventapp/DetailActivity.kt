package com.example.eventapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import com.example.eventapp.databinding.ActivityDetailBinding
import com.example.eventapp.util.Constants
import com.example.eventapp.util.Constants.EVENT_ID
import com.example.eventapp.util.Constants.FAVORITES_COLLECTION
import com.example.eventapp.util.Constants.USERS_COLLECTION
import com.example.eventapp.extension.ImageEnum
import com.example.eventapp.extension.getImageByRatio
import com.example.eventapp.extension.loadImage
import com.example.eventapp.viewmodel.DetailViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val viewModel: DetailViewModel by viewModels()
    private var map: GoogleMap? = null
    private lateinit var btnUrl: Button
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        btnUrl = findViewById(R.id.btnUrl)

        intent?.getStringExtra(Constants.EVENT_ID_KEY)?.let { eventId ->
            fetchEventDetails(eventId)
            checkIfFavorite(eventId)
        }

        binding.iconBackImage.setOnClickListener {
            finish()
        }

        binding.imageHeart.setOnClickListener {
            intent?.getStringExtra(Constants.EVENT_ID_KEY)?.let { eventId ->
                toggleFavorite(eventId)
            }
        }

        observeViewModel()
    }

    private fun fetchEventDetails(eventId: String) {
        viewModel.fetchEventDetails(eventId)
    }

    private fun observeViewModel() {
        viewModel.eventDetails.observe(this) { event ->
            event?.let { it ->
                with(binding) {
                    textActivityName.text = it.name
                    textAdrees.text = it.embedded.venues.firstOrNull()?.address?.line1 ?: "UNKNOWN"
                    textDate.text = it.dates.start.localDate
                    textEventType.text = it.classifications.firstOrNull()?.genre?.name
                    textTime.text = it.dates.start.localTime
                    imageViewActivity.loadImage(event.images.getImageByRatio(ImageEnum.IMAGE_16_9))

                    it.url.let { url ->
                        btnUrl.setOnClickListener {
                            openUrl(url)
                        }
                    }
                }

                val mapFragment =
                    supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
                        ?: SupportMapFragment.newInstance().also {
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.map, it)
                                .commit()
                        }

                it.embedded.venues.firstOrNull()?.location?.let { location ->
                    val latitudeStr = location.latitude
                    val longitudeStr = location.longitude
                    if (latitudeStr.isNotEmpty() && longitudeStr.isNotEmpty()) {
                        try {
                            val latitude = latitudeStr.toDouble()
                            val longitude = longitudeStr.toDouble()
                            mapFragment.getMapAsync { googleMap ->
                                map = googleMap
                                val locationVenue = LatLng(latitude, longitude)
                                map?.addMarker(
                                    MarkerOptions().position(locationVenue).title(it.name)
                                )
                                map?.moveCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                        locationVenue,
                                        12f
                                    )
                                )
                            }
                        } catch (e: NumberFormatException) {
                            Log.e("DetailActivity", "Invalid location data format", e)
                            showDefaultMap()
                        }
                    } else {
                        showDefaultMap()
                    }
                } ?: run {
                    showDefaultMap()
                }
            }
        }

        viewModel.loading.observe(this) {

        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Log.e("DetailActivity", "Error: $it")
            }
        }
    }


    private fun showDefaultMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
            ?: SupportMapFragment.newInstance().also {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.map, it)
                    .commit()
            }

        mapFragment.getMapAsync { googleMap ->
            map = googleMap
            googleMap.clear()
            val defaultLocation = LatLng(0.0, 0.0)
            googleMap.addMarker(
                MarkerOptions().position(defaultLocation).title("No Event Location")
            )
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 1f))
        }
    }

    private fun checkIfFavorite(eventId: String) {
        val userId = auth.currentUser?.uid ?: return
        val favoriteRef = firestore.collection(USERS_COLLECTION).document(userId)
            .collection(FAVORITES_COLLECTION).document(eventId)

        favoriteRef.get().addOnSuccessListener { document ->
            isFavorite = document.exists()
            updateFavoriteIcon(isFavorite)
        }
    }

    private fun toggleFavorite(eventId: String) {
        val userId = auth.currentUser?.uid ?: return
        val favoriteRef = firestore.collection(USERS_COLLECTION).document(userId)
            .collection(FAVORITES_COLLECTION).document(eventId)

        favoriteRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                favoriteRef.delete().addOnSuccessListener {
                    updateFavoriteIcon(false)
                }
            } else {
                favoriteRef.set(mapOf(EVENT_ID to eventId)).addOnSuccessListener {
                    updateFavoriteIcon(true)
                }
            }
        }
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        if (isFavorite) {
            binding.imageHeart.setImageResource(R.drawable.heart_color)
        } else {
            binding.imageHeart.setImageResource(R.drawable.heart)
        }
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}