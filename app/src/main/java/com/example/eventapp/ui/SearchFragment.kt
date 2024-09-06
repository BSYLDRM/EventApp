package com.example.eventapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.eventapp.adapter.EventsAdapter
import com.example.eventapp.databinding.FragmentSearchBinding
import com.example.eventapp.viewmodel.SearchViewModel

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private val viewModel: SearchViewModel by viewModels()
    private val adapter by lazy { EventsAdapter() } // Lazy initialization

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
        setupSearchListener()
    }

    private fun setupRecyclerView() {
        binding.recyclerSearch.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.searchResults.observe(viewLifecycleOwner) { events ->
            adapter.submitList(events)
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->


        }
    }

    private fun setupSearchListener() {
        binding.editTextSearch.doAfterTextChanged { searchText ->
            val query = searchText.toString().trim()
            if (query.isNotEmpty()) {
                viewModel.searchEvents(query)
            }
        }
    }
}


