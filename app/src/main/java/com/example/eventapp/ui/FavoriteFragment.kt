package com.example.eventapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventapp.adapter.EventsAdapter
import com.example.eventapp.databinding.FragmentFavoriteBinding
import com.example.eventapp.util.Constants
import com.example.eventapp.service.dataclass.Event
import com.example.eventapp.viewmodel.FavoriteViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FavoriteFragment : Fragment() {

    private lateinit var binding: FragmentFavoriteBinding
    private val viewModel: FavoriteViewModel by viewModels()
    private lateinit var adapter: EventsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)

        observeViewModel()
        fetchFavoritesFromFirebase()
        setupSwipeRefresh()
        fetchFavoritesFromFirebase()

        return binding.root
    }

    private fun setupRecyclerView(events: List<Event>) {
        adapter = EventsAdapter(
            eventsList = events,
            isHome = false,
            isFavorite = { eventId, callback ->
                viewModel.isFavorite(eventId, callback)
            },
            toggleFavorite = { event, callback ->
                viewModel.toggleFavorite(event) { isFav ->
                    callback(isFav)
                    if (!isFav) {

                        fetchFavoritesFromFirebase()
                    }
                }
            }
        )
        binding.recyclerFavorite.adapter = adapter
        binding.recyclerFavorite.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeViewModel() {
        viewModel.favoriteEvents.observe(viewLifecycleOwner) { events ->
            setupRecyclerView(events)
            binding.swipeRefreshLayout.isRefreshing = false

            if (events.isEmpty()) {
                binding.lottieNoData.visibility = View.VISIBLE
                binding.recyclerFavorite.visibility = View.GONE
            } else {
                binding.lottieNoData.visibility = View.GONE
                binding.recyclerFavorite.visibility = View.VISIBLE
            }
        }
    }

    private fun fetchFavoritesFromFirebase() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection(Constants.USERS_COLLECTION)
            .document(userId)
            .collection(Constants.FAVORITES_COLLECTION)
            .get()
            .addOnSuccessListener { documents ->
                val favoriteIds = documents.map { it.id }
                viewModel.fetchFavoriteEvents(favoriteIds)
            }

    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchFavoritesFromFirebase()
        }
    }
}