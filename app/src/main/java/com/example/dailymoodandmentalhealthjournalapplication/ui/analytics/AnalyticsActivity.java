package com.example.dailymoodandmentalhealthjournalapplication.ui.analytics;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.dailymoodandmentalhealthjournalapplication.R;
import com.example.dailymoodandmentalhealthjournalapplication.auth.LocalAuthManager;
import com.example.dailymoodandmentalhealthjournalapplication.databinding.ActivityAnalyticsBinding;
import com.example.dailymoodandmentalhealthjournalapplication.ui.viewmodels.JournalViewModel;
import com.example.dailymoodandmentalhealthjournalapplication.ui.viewmodels.MoodViewModel;
import com.example.dailymoodandmentalhealthjournalapplication.utils.DataExportManager;

import java.io.IOException;

/**
 * Activity for displaying mood analytics and insights.
 */
public class AnalyticsActivity extends AppCompatActivity {
    private ActivityAnalyticsBinding binding;
    private MoodViewModel moodViewModel;
    private JournalViewModel journalViewModel;
    private LocalAuthManager authManager;
    private DataExportManager dataExportManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set up view binding
        binding = ActivityAnalyticsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Set up toolbar
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.analytics);
        
        // Initialize view models and managers
        moodViewModel = new ViewModelProvider(this).get(MoodViewModel.class);
        journalViewModel = new ViewModelProvider(this).get(JournalViewModel.class);
        authManager = LocalAuthManager.getInstance(this);
        dataExportManager = new DataExportManager(this);
        
        // Set up tabs
        setupTabs();
        
        // Set up export buttons
        setupExportButtons();
        
        // Load weekly mood data by default
        loadWeeklyMoodData();
    }

    private void setupTabs() {
        // Set up tab selection listeners
        binding.buttonWeekly.setOnClickListener(v -> {
            setActiveTab(binding.buttonWeekly);
            loadWeeklyMoodData();
        });
        
        binding.buttonMonthly.setOnClickListener(v -> {
            setActiveTab(binding.buttonMonthly);
            loadMonthlyMoodData();
        });
        
        binding.buttonDistribution.setOnClickListener(v -> {
            setActiveTab(binding.buttonDistribution);
            loadMoodDistribution();
        });
        
        binding.buttonEmotions.setOnClickListener(v -> {
            setActiveTab(binding.buttonEmotions);
            loadCommonEmotions();
        });
    }

    private void setActiveTab(View activeTab) {
        // Reset all tabs
        binding.buttonWeekly.setBackgroundTintList(getColorStateList(R.color.light_gray));
        binding.buttonMonthly.setBackgroundTintList(getColorStateList(R.color.light_gray));
        binding.buttonDistribution.setBackgroundTintList(getColorStateList(R.color.light_gray));
        binding.buttonEmotions.setBackgroundTintList(getColorStateList(R.color.light_gray));
        
        // Set active tab
        activeTab.setBackgroundTintList(getColorStateList(R.color.primary_light));
    }

    private void setupExportButtons() {
        binding.buttonExportCsv.setOnClickListener(v -> exportDataToCsv());
        binding.buttonExportPdf.setOnClickListener(v -> {
            // PDF export not implemented yet
            Toast.makeText(this, R.string.feature_not_implemented, Toast.LENGTH_SHORT).show();
        });
    }

    private void loadWeeklyMoodData() {
        binding.textViewChartTitle.setText(R.string.weekly_mood);
        binding.progressBar.setVisibility(View.VISIBLE);
        
        // In a real implementation, this would load data into a chart
        // For now, we'll just show a placeholder message
        binding.textViewNoData.setText(R.string.weekly_mood_placeholder);
        binding.textViewNoData.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
        
        // This would be implemented with MPAndroidChart
        // Example:
        // LineChart chart = binding.chartWeekly;
        // List<Entry> entries = new ArrayList<>();
        // ... add data points ...
        // LineDataSet dataSet = new LineDataSet(entries, "Weekly Mood");
        // LineData lineData = new LineData(dataSet);
        // chart.setData(lineData);
        // chart.invalidate();
    }

    private void loadMonthlyMoodData() {
        binding.textViewChartTitle.setText(R.string.monthly_mood);
        binding.progressBar.setVisibility(View.VISIBLE);
        
        // In a real implementation, this would load data into a chart
        // For now, we'll just show a placeholder message
        binding.textViewNoData.setText(R.string.monthly_mood_placeholder);
        binding.textViewNoData.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
    }

    private void loadMoodDistribution() {
        binding.textViewChartTitle.setText(R.string.mood_distribution);
        binding.progressBar.setVisibility(View.VISIBLE);
        
        // In a real implementation, this would load data into a chart
        // For now, we'll just show a placeholder message
        binding.textViewNoData.setText(R.string.mood_distribution_placeholder);
        binding.textViewNoData.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
    }

    private void loadCommonEmotions() {
        binding.textViewChartTitle.setText(R.string.common_emotions);
        binding.progressBar.setVisibility(View.VISIBLE);
        
        // In a real implementation, this would load data into a chart
        // For now, we'll just show a placeholder message
        binding.textViewNoData.setText(R.string.common_emotions_placeholder);
        binding.textViewNoData.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
    }

    private void exportDataToCsv() {
        binding.progressBar.setVisibility(View.VISIBLE);
        
        // Get user ID
        String userId = authManager.getCurrentUserId();
        if (userId == null) {
            Toast.makeText(this, R.string.error_exporting_data, Toast.LENGTH_SHORT).show();
            binding.progressBar.setVisibility(View.GONE);
            return;
        }
        
        // Get mood entries
        moodViewModel.getAllMoodEntries().observe(this, moodEntries -> {
            if (moodEntries != null && !moodEntries.isEmpty()) {
                try {
                    // Export mood entries to CSV
                    Uri fileUri = dataExportManager.exportMoodEntriesToCSV(moodEntries);
                    
                    // Share the file
                    startActivity(dataExportManager.createShareIntent(fileUri, "text/csv"));
                } catch (IOException e) {
                    Toast.makeText(this, R.string.error_exporting_data, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, R.string.no_data_to_export, Toast.LENGTH_SHORT).show();
            }
            binding.progressBar.setVisibility(View.GONE);
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
