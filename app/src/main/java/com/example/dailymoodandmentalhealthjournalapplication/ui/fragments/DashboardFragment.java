package com.example.dailymoodandmentalhealthjournalapplication.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.dailymoodandmentalhealthjournalapplication.R;
import com.example.dailymoodandmentalhealthjournalapplication.databinding.FragmentDashboardBinding;
import com.example.dailymoodandmentalhealthjournalapplication.ui.analytics.AnalyticsActivity;
import com.example.dailymoodandmentalhealthjournalapplication.ui.viewmodels.MoodViewModel;
import com.example.dailymoodandmentalhealthjournalapplication.ui.viewmodels.UserViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Dashboard fragment showing the user's mood summary and recent entries.
 */
public class DashboardFragment extends Fragment {
    private FragmentDashboardBinding binding;
    private UserViewModel userViewModel;
    private MoodViewModel moodViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize view models
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        moodViewModel = new ViewModelProvider(requireActivity()).get(MoodViewModel.class);

        // Set up the dashboard
        setupDashboard();

        // Set up view analytics button
        binding.buttonViewAnalytics.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), AnalyticsActivity.class));
        });
    }

    private void setupDashboard() {
        // Set today's date
        binding.textViewDate.setText(getTodayDateFormatted());

        // Observe user data
        userViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                // Set user name
                binding.textViewWelcome.setText(getString(R.string.welcome_user, user.getName()));
            }
        });

        // Observe mood data for the current week
        moodViewModel.getMoodEntriesForCurrentWeek().observe(getViewLifecycleOwner(), moodEntries -> {
            if (moodEntries != null && !moodEntries.isEmpty()) {
                // Update mood summary
                binding.textViewMoodSummary.setText(getString(R.string.mood_entries_this_week, moodEntries.size()));
                binding.textViewNoData.setVisibility(View.GONE);
                binding.cardViewMoodSummary.setVisibility(View.VISIBLE);
            } else {
                // No mood data
                binding.textViewNoData.setVisibility(View.VISIBLE);
                binding.cardViewMoodSummary.setVisibility(View.GONE);
            }
        });

        // Observe average mood intensity for the current week
        moodViewModel.getAverageMoodIntensity(getStartOfWeek(), getEndOfWeek()).observe(getViewLifecycleOwner(), averageIntensity -> {
            if (averageIntensity != null) {
                binding.textViewAverageMood.setText(getString(R.string.average_mood_intensity, averageIntensity));
            }
        });
    }

    private String getTodayDateFormatted() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    private long getStartOfWeek() {
        // Get start of current week in milliseconds
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private long getEndOfWeek() {
        // Get end of current week in milliseconds
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek() + 6);
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 23);
        calendar.set(java.util.Calendar.MINUTE, 59);
        calendar.set(java.util.Calendar.SECOND, 59);
        calendar.set(java.util.Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
