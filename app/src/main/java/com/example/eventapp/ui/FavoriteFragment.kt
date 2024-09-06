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

        setupRecyclerView()
        observeViewModel()
        fetchFavoritesFromFirebase()
        setupSwipeRefresh()
        showLoadingAnimation(true)
        fetchFavoritesFromFirebase()

        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = EventsAdapter()
        binding.recyclerFavorite.adapter = adapter
        binding.recyclerFavorite.layoutManager = LinearLayoutManager(requireContext())

    }

    private fun observeViewModel() {
        viewModel.favoriteEvents.observe(viewLifecycleOwner) { events ->
            adapter.submitList(events)
            showLoadingAnimation(false)
            binding.swipeRefreshLayout.isRefreshing = false
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
            .addOnFailureListener {
                // Verileri yükleme başarısız olduysa animasyonu gizle
                showLoadingAnimation(false)
            }
    }
    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            fetchFavoritesFromFirebase()
        }
    }
    private fun showLoadingAnimation(show: Boolean) {
        if (show) {
            // Lottie animasyonunu göster
            binding.lottieAnimationView.visibility = View.VISIBLE
        } else {
            // Lottie animasyonunu gizle
            binding.lottieAnimationView.visibility = View.GONE
        }
    }
    object Constants {
        const val USERS_COLLECTION = "users"
        const val FAVORITES_COLLECTION = "favorites"
        const val EVENT_ID_KEY = "event_id"
    }
}
