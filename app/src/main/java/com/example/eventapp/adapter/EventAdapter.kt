package com.example.eventapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eventapp.R
import com.example.eventapp.databinding.ItemEventBinding
import com.example.eventapp.service.dataclass.Event
import com.example.eventapp.ui.DetailActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference

class EventsAdapter(
    private var eventsList: List<Event> = emptyList()
) : RecyclerView.Adapter<EventsAdapter.EventsViewHolder>() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val favoriteEventsList = mutableListOf<Event>()

    inner class EventsViewHolder(private val binding: ItemEventBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(event: Event) {
            binding.textViewName.text = event.name
            binding.textViewCity.text = event.embedded.venues.firstOrNull()?.city?.name

            event.images
                .filter { it.ratio == "3_2" }
                .maxByOrNull { it.width }
                ?.let { image ->
                    Glide.with(binding.imageActivity.context)
                        .load(image.url)
                        .into(binding.imageActivity)
                }

            handleFavorite(event)
            binding.root.setOnClickListener {
                navigateToDetail(event.id)
            }
        }

        private fun handleFavorite(event: Event) {
            val userId = auth.currentUser?.uid ?: return
            val favoriteRef = firestore.collection(USERS_COLLECTION).document(userId)
                .collection(FAVORITES_COLLECTION).document(event.id)

            favoriteRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    binding.imageHeart.setImageResource(R.drawable.heart_color)
                    favoriteEventsList.add(event)
                } else {
                    binding.imageHeart.setImageResource(R.drawable.heart)
                    favoriteEventsList.remove(event)
                }
            }

            binding.imageHeart.setOnClickListener {
                toggleFavorite(event, favoriteRef)
            }
        }

        private fun toggleFavorite(event: Event, favoriteRef: DocumentReference) {
            favoriteRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    favoriteRef.delete().addOnSuccessListener {
                        binding.imageHeart.setImageResource(R.drawable.heart)
                        favoriteEventsList.remove(event)
                    }
                } else {
                    favoriteRef.set(mapOf("id" to event.id)).addOnSuccessListener {
                        binding.imageHeart.setImageResource(R.drawable.heart_color)
                        favoriteEventsList.add(event)
                    }
                }
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

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val FAVORITES_COLLECTION = "favorites"
        private const val EVENT_ID_KEY = "event_id"
    }
}
