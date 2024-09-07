package com.example.eventapp.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventapp.adapter.EventsAdapter
import com.example.eventapp.databinding.FragmentSearchBinding
import com.example.eventapp.viewmodel.SearchViewModel

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var adapter: EventsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView ve adaptör ayarları
        adapter = EventsAdapter()
        binding.recyclerSearch.adapter = adapter
        binding.recyclerSearch.layoutManager = LinearLayoutManager(requireContext())

        // Arama işlemi için EditText üzerinde dinleyici ayarla
        binding.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()

                // En az 3 karakter girildiğinde arama yapılacak
                if (query.length >= 3) {
                    viewModel.searchEvents(city = query)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // ViewModel'den gelen verileri gözlemle
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.events.observe(viewLifecycleOwner) { eventsResponse ->
            if (eventsResponse?.embedded?.events != null) {
                // RecyclerView adaptörüne verileri ekle
                adapter.submitList(eventsResponse.embedded.events)
            } else {
                // Null durumunda boş bir liste göster
                adapter.submitList(emptyList())
            }
        }

        // Hata mesajlarını gözlemle
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }
    }
}


