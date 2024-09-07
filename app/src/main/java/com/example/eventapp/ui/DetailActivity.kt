package com.example.eventapp.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.example.eventapp.R
import com.example.eventapp.databinding.ActivityDetailBinding
import com.example.eventapp.viewmodel.DetailViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val viewModel: DetailViewModel by viewModels()
    private lateinit var map: GoogleMap
    private lateinit var btnUrl: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        btnUrl = findViewById(R.id.btnUrl)
        observeViewModel()
        intent?.getStringExtra("event_id")?.let {
            fetchEventDetails(it)
        }

        binding.iconBackImage.setOnClickListener {
            finish()
        }
    }



    private fun fetchEventDetails(eventId: String) {
        viewModel.fetchEventDetails(eventId)
    }

    private fun observeViewModel() {
        viewModel.eventDetails.observe(this) { event ->
            event?.let {
                binding.textActivityName.text = it.name
                binding.textAdrees.text = it.embedded.venues.firstOrNull()?.address?.line1
                binding.textDate.text = it.dates.start.localDate
                binding.textCity.text = it.embedded.venues.firstOrNull()?.city?.name
                binding.textTime.text = it.dates.start.localTime

                it.url.let { url ->
                    btnUrl.setOnClickListener {
                        openUrl(url)
                    }
                }
                event.images
                    .filter { it.ratio == "4_3" }
                    .maxByOrNull { it.width }
                    ?.let { image ->
                        Glide.with(this)
                            .load(image.url)
                            .into(binding.imageViewActivity)
                    }

                val mapFragment =
                    supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
                        ?: SupportMapFragment.newInstance().also {
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.map, it)
                                .commit()
                        }

                mapFragment.getMapAsync { googleMap ->
                    map = googleMap
                    it.embedded.venues.firstOrNull()?.location?.let { location ->
                        val locationVenue =
                            LatLng(location.latitude.toDouble(), location.longitude.toDouble())
                        map.addMarker(
                            MarkerOptions().position(locationVenue).title("Event Location")
                        )
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(locationVenue, 12f))
                    }
                }
            }
        }

        viewModel.loading.observe(this) {

        }

        viewModel.error.observe(this) { error ->
            error?.let {
                Log.e("DetailActivity", "Hata: $it")
            }
        }
    }
    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)}
}