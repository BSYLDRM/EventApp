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
import com.example.eventapp.service.dataclass.Event
import com.example.eventapp.viewmodel.FavoriteViewModel

class FavoriteFragment : Fragment() {

    private lateinit var binding: FragmentFavoriteBinding
    private val viewModel: FavoriteViewModel by viewModels()
    private lateinit var adapter: EventsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)

        viewModel.fetchFavoritesFromFirebase()
        observeViewModel()
        setupSwipeRefresh()

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
                        viewModel.fetchFavoritesFromFirebase()
                    }
                }
            }
        )
        binding.recyclerFavorite.adapter = adapter
        binding.recyclerFavorite.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeViewModel() = with(binding) {
        viewModel.favoriteEvents.observe(viewLifecycleOwner) { events ->
            setupRecyclerView(events)
            swipeRefreshLayout.isRefreshing = false

            if (events.isEmpty()) {
                lottieNoData.visibility = View.VISIBLE
                recyclerFavorite.visibility = View.GONE
            } else {
                lottieNoData.visibility = View.GONE
                recyclerFavorite.visibility = View.VISIBLE
            }
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.fetchFavoritesFromFirebase()
        }
    }
}