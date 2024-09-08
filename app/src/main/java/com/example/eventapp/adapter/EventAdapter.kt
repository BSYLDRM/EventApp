package com.example.eventapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.eventapp.R
import com.example.eventapp.databinding.ItemEventBinding
import com.example.eventapp.extension.Constants.EVENT_ID_KEY
import com.example.eventapp.extension.ImageEnum
import com.example.eventapp.extension.getImageByRatio
import com.example.eventapp.extension.loadImage
import com.example.eventapp.service.dataclass.Event
import com.example.eventapp.ui.DetailActivity

class EventsAdapter(
    private var eventsList: List<Event>,
    private val isFavorite: (String, (Boolean) -> Unit) -> Unit,
    private val toggleFavorite: (Event, (Boolean) -> Unit) -> Unit
) : RecyclerView.Adapter<EventsAdapter.EventsViewHolder>() {

    inner class EventsViewHolder(private val binding: ItemEventBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event) {
            binding.textViewName.text = event.name
            binding.textViewCity.text = event.embedded.venues.firstOrNull()?.city?.name

            binding.imageActivity.loadImage(event.images.getImageByRatio(ImageEnum.IMAGE_4_3))

            isFavorite(event.id) { isFav ->
                updateHeartIcon(isFav)
            }

            binding.imageHeart.setOnClickListener {
                toggleFavorite(event) { isFav ->
                    updateHeartIcon(isFav)
                }
            }

            binding.root.setOnClickListener {
                navigateToDetail(event.id)
            }
        }

        private fun updateHeartIcon(isFav: Boolean) {
            if (isFav) {
                binding.imageHeart.setImageResource(R.drawable.heart_color)
            } else {
                binding.imageHeart.setImageResource(R.drawable.heart)
            }
        }

        private fun navigateToDetail(eventId: String) {
            val intent = Intent(binding.root.context, DetailActivity::class.java)
            intent.putExtra(EVENT_ID_KEY, eventId)
            binding.root.context.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventsViewHolder, position: Int) {
        holder.bind(eventsList[position])
    }

    override fun getItemCount(): Int = eventsList.size

    fun submitList(newEvents: List<Event>) {
        eventsList = newEvents
        notifyDataSetChanged()
    }
}