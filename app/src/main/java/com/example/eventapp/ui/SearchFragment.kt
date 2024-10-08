package com.example.eventapp.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventapp.adapter.EventsAdapter
import com.example.eventapp.databinding.FragmentSearchBinding
import com.example.eventapp.extension.showToast
import com.example.eventapp.extension.visibilityGone
import com.example.eventapp.extension.visibilityVisible
import com.example.eventapp.viewmodel.FavoriteViewModel
import com.example.eventapp.viewmodel.SearchViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private val viewModel: SearchViewModel by viewModels()
    private val favoriteViewModel: FavoriteViewModel by viewModels()
    private lateinit var adapter: EventsAdapter
    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = EventsAdapter(
            eventsList = emptyList(),
            isHome = false,
            isFavorite = { eventId, callback ->
                favoriteViewModel.isFavorite(eventId, callback)
            },
            toggleFavorite = { event, callback ->
                favoriteViewModel.toggleFavorite(event, callback)
            }
        )
        binding.recyclerSearch.adapter = adapter
        binding.recyclerSearch.layoutManager = LinearLayoutManager(requireContext())

        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()

                searchJob?.cancel()
                searchJob = lifecycleScope.launch {
                    delay(500)
                    if (query.isNotBlank()) {
                        viewModel.searchEvents(city = query)
                    } else {
                        adapter.submitList(emptyList())
                        binding.lottieNoData.visibilityVisible()
                        binding.recyclerSearch.visibilityGone()
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.events.observe(viewLifecycleOwner) { eventsResponse ->
            with(binding) {
                if (eventsResponse?.embedded == null) {
                    lottieNoData.visibilityVisible()
                    recyclerSearch.visibilityGone()
                } else {
                    lottieNoData.visibilityGone()
                    recyclerSearch.visibilityVisible()
                    adapter.submitList(eventsResponse.embedded.events)
                }
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            requireContext().showToast(errorMessage)
        }
    }
}