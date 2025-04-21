package com.example.dailymoodandmentalhealthjournalapplication.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.dailymoodandmentalhealthjournalapplication.R;
import com.example.dailymoodandmentalhealthjournalapplication.databinding.FragmentJournalBinding;
import com.example.dailymoodandmentalhealthjournalapplication.ui.adapters.JournalEntryAdapter;
import com.example.dailymoodandmentalhealthjournalapplication.ui.journal.JournalEntryActivity;
import com.example.dailymoodandmentalhealthjournalapplication.ui.viewmodels.JournalViewModel;

/**
 * Fragment for displaying and managing journal entries.
 */
public class JournalFragment extends Fragment {
    private FragmentJournalBinding binding;
    private JournalViewModel journalViewModel;
    private JournalEntryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentJournalBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize view model
        journalViewModel = new ViewModelProvider(requireActivity()).get(JournalViewModel.class);

        // Set up the recycler view
        setupRecyclerView();

        // Set up the search view
        setupSearchView();

        // Set up the new entry button
        binding.fabNewEntry.setOnClickListener(v -> {
            // Open journal entry activity for creating a new entry
            Intent intent = new Intent(requireContext(), JournalEntryActivity.class);
            intent.putExtra("isNewEntry", true);
            startActivity(intent);
        });

        // Set up filter buttons
        setupFilterButtons();

        // Load journal entries
        loadJournalEntries();
    }

    private void setupRecyclerView() {
        adapter = new JournalEntryAdapter(entry -> {
            // Handle journal entry click
            Intent intent = new Intent(requireContext(), JournalEntryActivity.class);
            intent.putExtra("isNewEntry", false);
            intent.putExtra("entryId", entry.getId());
            startActivity(intent);
        }, (entry, isFavorite) -> {
            // Handle favorite toggle
            journalViewModel.updateFavoriteStatus(entry.getId(), isFavorite);
            Toast.makeText(requireContext(), 
                    isFavorite ? R.string.added_to_favorites : R.string.removed_from_favorites, 
                    Toast.LENGTH_SHORT).show();
        });

        binding.recyclerViewJournalEntries.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewJournalEntries.setAdapter(adapter);
    }

    private void setupSearchView() {
        binding.searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchJournalEntries(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    loadJournalEntries();
                }
                return false;
            }
        });
    }

    private void setupFilterButtons() {
        binding.buttonFilterDate.setOnClickListener(v -> {
            // Show date filter dialog
            // This would be implemented with a DatePickerDialog
            Toast.makeText(requireContext(), "Date filter not implemented yet", Toast.LENGTH_SHORT).show();
        });

        binding.buttonFilterMood.setOnClickListener(v -> {
            // Show mood filter dialog
            // This would be implemented with a custom dialog showing mood options
            Toast.makeText(requireContext(), "Mood filter not implemented yet", Toast.LENGTH_SHORT).show();
        });

        binding.buttonFilterTag.setOnClickListener(v -> {
            // Show tag filter dialog
            // This would be implemented with a custom dialog showing tag options
            Toast.makeText(requireContext(), "Tag filter not implemented yet", Toast.LENGTH_SHORT).show();
        });

        binding.buttonViewFavorites.setOnClickListener(v -> {
            // Load favorite entries
            loadFavoriteEntries();
        });
    }

    private void loadJournalEntries() {
        binding.progressBar.setVisibility(View.VISIBLE);
        journalViewModel.getAllJournalEntries().observe(getViewLifecycleOwner(), journalEntries -> {
            binding.progressBar.setVisibility(View.GONE);
            if (journalEntries != null && !journalEntries.isEmpty()) {
                adapter.submitList(journalEntries);
                binding.textViewNoEntries.setVisibility(View.GONE);
            } else {
                binding.textViewNoEntries.setVisibility(View.VISIBLE);
            }
        });
    }

    private void searchJournalEntries(String query) {
        binding.progressBar.setVisibility(View.VISIBLE);
        journalViewModel.searchJournalEntries(query).observe(getViewLifecycleOwner(), journalEntries -> {
            binding.progressBar.setVisibility(View.GONE);
            if (journalEntries != null && !journalEntries.isEmpty()) {
                adapter.submitList(journalEntries);
                binding.textViewNoEntries.setVisibility(View.GONE);
            } else {
                adapter.submitList(null);
                binding.textViewNoEntries.setText(R.string.no_search_results);
                binding.textViewNoEntries.setVisibility(View.VISIBLE);
            }
        });
    }

    private void loadFavoriteEntries() {
        binding.progressBar.setVisibility(View.VISIBLE);
        journalViewModel.getFavoriteJournalEntries().observe(getViewLifecycleOwner(), journalEntries -> {
            binding.progressBar.setVisibility(View.GONE);
            if (journalEntries != null && !journalEntries.isEmpty()) {
                adapter.submitList(journalEntries);
                binding.textViewNoEntries.setVisibility(View.GONE);
            } else {
                adapter.submitList(null);
                binding.textViewNoEntries.setText(R.string.no_favorite_entries);
                binding.textViewNoEntries.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
