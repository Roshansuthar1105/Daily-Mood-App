package com.example.dailymoodandmentalhealthjournalapplication.ui.mood;

import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.dailymoodandmentalhealthjournalapplication.R;
import com.example.dailymoodandmentalhealthjournalapplication.databinding.ActivityMoodSelectionBinding;
import com.example.dailymoodandmentalhealthjournalapplication.ui.viewmodels.MoodViewModel;

/**
 * Activity for selecting and logging a mood.
 */
public class MoodSelectionActivity extends AppCompatActivity {
    private ActivityMoodSelectionBinding binding;
    private MoodViewModel moodViewModel;
    private String selectedMoodType = null;
    private int moodIntensity = 5; // Default intensity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set up view binding
        binding = ActivityMoodSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Set up toolbar
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.mood_tracking);
        
        // Initialize view model
        moodViewModel = new ViewModelProvider(this).get(MoodViewModel.class);
        
        // Set up mood selection buttons
        setupMoodButtons();
        
        // Set up intensity slider
        setupIntensitySlider();
        
        // Set up save button
        binding.buttonSaveMood.setOnClickListener(v -> saveMood());
    }

    private void setupMoodButtons() {
        // Happy mood
        binding.cardViewHappy.setOnClickListener(v -> {
            selectMood("HAPPY");
            highlightSelectedMood(binding.cardViewHappy);
        });
        
        // Sad mood
        binding.cardViewSad.setOnClickListener(v -> {
            selectMood("SAD");
            highlightSelectedMood(binding.cardViewSad);
        });
        
        // Angry mood
        binding.cardViewAngry.setOnClickListener(v -> {
            selectMood("ANGRY");
            highlightSelectedMood(binding.cardViewAngry);
        });
        
        // Anxious mood
        binding.cardViewAnxious.setOnClickListener(v -> {
            selectMood("ANXIOUS");
            highlightSelectedMood(binding.cardViewAnxious);
        });
        
        // Neutral mood
        binding.cardViewNeutral.setOnClickListener(v -> {
            selectMood("NEUTRAL");
            highlightSelectedMood(binding.cardViewNeutral);
        });
    }

    private void selectMood(String moodType) {
        selectedMoodType = moodType;
        binding.textViewIntensityLabel.setVisibility(View.VISIBLE);
        binding.seekBarIntensity.setVisibility(View.VISIBLE);
        binding.textViewIntensityValue.setVisibility(View.VISIBLE);
        binding.textInputLayoutNotes.setVisibility(View.VISIBLE);
        binding.buttonSaveMood.setVisibility(View.VISIBLE);
    }

    private void highlightSelectedMood(View selectedCard) {
        // Reset all cards
        binding.cardViewHappy.setCardElevation(getResources().getDimension(R.dimen.card_elevation_normal));
        binding.cardViewSad.setCardElevation(getResources().getDimension(R.dimen.card_elevation_normal));
        binding.cardViewAngry.setCardElevation(getResources().getDimension(R.dimen.card_elevation_normal));
        binding.cardViewAnxious.setCardElevation(getResources().getDimension(R.dimen.card_elevation_normal));
        binding.cardViewNeutral.setCardElevation(getResources().getDimension(R.dimen.card_elevation_normal));
        
        // Highlight selected card
        selectedCard.setElevation(getResources().getDimension(R.dimen.card_elevation_selected));
    }

    private void setupIntensitySlider() {
        binding.seekBarIntensity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                moodIntensity = progress + 1; // 1-10 scale
                binding.textViewIntensityValue.setText(String.valueOf(moodIntensity));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not needed
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Not needed
            }
        });
    }
    private void saveMood() {
        if (selectedMoodType == null) {
            Toast.makeText(this, R.string.select_mood_first, Toast.LENGTH_SHORT).show();
            return;
        }
        
        String notes = binding.editTextNotes.getText().toString().trim();
        
        // Show progress indicator
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.buttonSaveMood.setEnabled(false);
        
        // Save mood entry with detailed logging
        android.util.Log.d("MoodSelectionActivity", "Saving mood: " + selectedMoodType + ", intensity: " + moodIntensity);
        
        try {
            moodViewModel.addMoodEntry(selectedMoodType, moodIntensity, notes, id -> {
                // Hide progress indicator
                binding.progressBar.setVisibility(View.GONE);
                binding.buttonSaveMood.setEnabled(true);
                
                android.util.Log.d("MoodSelectionActivity", "Mood save result ID: " + id);
                
                if (id != -1) {
                    Toast.makeText(MoodSelectionActivity.this, R.string.mood_saved, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(MoodSelectionActivity.this, R.string.error_saving_mood, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            android.util.Log.e("MoodSelectionActivity", "Error saving mood: " + e.getMessage(), e);
            binding.progressBar.setVisibility(View.GONE);
            binding.buttonSaveMood.setEnabled(true);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
