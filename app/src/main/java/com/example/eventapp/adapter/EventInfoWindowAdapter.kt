package com.example.eventapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.example.eventapp.databinding.ItemMapEventBinding
import com.example.eventapp.extension.ImageEnum
import com.example.eventapp.extension.getImageByRatio
import com.example.eventapp.extension.loadImage
import com.example.eventapp.service.dataclass.Event
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class EventInfoWindowAdapter(
    private val context: Context,
    private val onInfoWindowClick: (Event) -> Unit
) : GoogleMap.InfoWindowAdapter {

    private lateinit var binding: ItemMapEventBinding

    override fun getInfoWindow(marker: Marker): View? {
        return null
    }

    override fun getInfoContents(marker: Marker): View {
        binding = ItemMapEventBinding.inflate(LayoutInflater.from(context))

        binding.apply {
            val event = marker.tag as? Event ?: return root
            textViewName.text = event.name
            textViewCity.text = event.embedded.venues.firstOrNull()?.city?.name
            imageActivity.loadImage(event.images.getImageByRatio(ImageEnum.IMAGE_16_9))


            root.setOnClickListener {
                onInfoWindowClick(event)
            }
        }

        return binding.root
    }
}
