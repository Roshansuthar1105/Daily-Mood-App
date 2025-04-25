package com.example.dailymoodandmentalhealthjournalapplication.ui.journal;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.dailymoodandmentalhealthjournalapplication.R;
import com.example.dailymoodandmentalhealthjournalapplication.data.entity.JournalEntry;
import com.example.dailymoodandmentalhealthjournalapplication.databinding.ActivityJournalEntryBinding;
import com.example.dailymoodandmentalhealthjournalapplication.ui.viewmodels.JournalViewModel;
import com.example.dailymoodandmentalhealthjournalapplication.utils.SentimentAnalyzer;

import java.util.Map;

/**
 * Activity for creating or editing a journal entry.
 */
public class JournalEntryActivity extends AppCompatActivity {
    private ActivityJournalEntryBinding binding;
    private JournalViewModel journalViewModel;
    private boolean isNewEntry;
    private long entryId;
    private JournalEntry currentEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up view binding
        binding = ActivityJournalEntryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up toolbar
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize view model
        journalViewModel = new ViewModelProvider(this).get(JournalViewModel.class);

        // Get intent data
        isNewEntry = getIntent().getBooleanExtra("isNewEntry", true);
        if (!isNewEntry) {
            entryId = getIntent().getLongExtra("entryId", -1);
            if (entryId == -1) {
                Toast.makeText(this, R.string.error_loading_entry, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }

        // Set up the UI based on whether this is a new entry or editing an existing one
        setupUI();

        // Set up save button
        binding.buttonSaveEntry.setOnClickListener(v -> saveJournalEntry());
    }

    private void setupUI() {
        if (isNewEntry) {
            // New entry
            getSupportActionBar().setTitle(R.string.new_entry);
            binding.buttonDeleteEntry.setVisibility(View.GONE);
            binding.cardViewSentiment.setVisibility(View.GONE);
        } else {
            // Edit existing entry
            getSupportActionBar().setTitle(R.string.edit_entry);
            binding.buttonDeleteEntry.setVisibility(View.VISIBLE);
            binding.buttonDeleteEntry.setOnClickListener(v -> showDeleteConfirmationDialog());

            // Load entry data
            loadJournalEntry();
        }
    }

    private void loadJournalEntry() {
        binding.progressBar.setVisibility(View.VISIBLE);
        journalViewModel.getJournalEntryById(entryId).observe(this, journalEntry -> {
            binding.progressBar.setVisibility(View.GONE);
            if (journalEntry != null) {
                currentEntry = journalEntry;

                // Set entry data
                binding.editTextTitle.setText(journalEntry.getTitle());
                binding.editTextContent.setText(journalEntry.getContent());
                binding.editTextTags.setText(journalEntry.getTags());

                // Analyze sentiment
                analyzeSentiment(journalEntry.getContent());
            } else {
                Toast.makeText(this, R.string.error_loading_entry, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void analyzeSentiment(String content) {
        if (content == null || content.isEmpty()) {
            binding.cardViewSentiment.setVisibility(View.GONE);
            return;
        }

        // Get sentiment score
        float sentimentScore = journalViewModel.analyzeSentiment(content);

        // Get emotions
        Map<String, Integer> emotions = journalViewModel.getEmotions(content, 3);

        if (sentimentScore != 0 || !emotions.isEmpty()) {
            binding.cardViewSentiment.setVisibility(View.VISIBLE);

            // Set sentiment score
            String sentimentText;
            if (sentimentScore > 0.5) {
                sentimentText = getString(R.string.sentiment_very_positive);
            } else if (sentimentScore > 0) {
                sentimentText = getString(R.string.sentiment_positive);
            } else if (sentimentScore == 0) {
                sentimentText = getString(R.string.sentiment_neutral);
            } else if (sentimentScore > -0.5) {
                sentimentText = getString(R.string.sentiment_negative);
            } else {
                sentimentText = getString(R.string.sentiment_very_negative);
            }
            binding.textViewSentiment.setText(sentimentText);

            // Set emotions
            if (!emotions.isEmpty()) {
                StringBuilder emotionsText = new StringBuilder();
                for (Map.Entry<String, Integer> emotion : emotions.entrySet()) {
                    emotionsText.append(emotion.getKey()).append(", ");
                }
                if (emotionsText.length() > 2) {
                    emotionsText.delete(emotionsText.length() - 2, emotionsText.length());
                }
                binding.textViewEmotions.setText(emotionsText.toString());
                binding.textViewEmotions.setVisibility(View.VISIBLE);
                binding.textViewEmotionsLabel.setVisibility(View.VISIBLE);
            } else {
                binding.textViewEmotions.setVisibility(View.GONE);
                binding.textViewEmotionsLabel.setVisibility(View.GONE);
            }
        } else {
            binding.cardViewSentiment.setVisibility(View.GONE);
        }
    }

    private void saveJournalEntry() {
        String title = binding.editTextTitle.getText().toString().trim();
        String content = binding.editTextContent.getText().toString().trim();
        String tags = binding.editTextTags.getText().toString().trim();

        // Validate input
        if (title.isEmpty()) {
            binding.editTextTitle.setError(getString(R.string.title_required));
            return;
        }

        if (content.isEmpty()) {
            binding.editTextContent.setError(getString(R.string.content_required));
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.buttonSaveEntry.setEnabled(false);

        if (isNewEntry) {
            // Create new entry using the callback pattern
            journalViewModel.addJournalEntry(title, content, tags, id -> {
                binding.progressBar.setVisibility(View.GONE);
                binding.buttonSaveEntry.setEnabled(true);

                if (id != -1) {
                    Toast.makeText(this, R.string.entry_saved, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, R.string.error_saving_entry, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Update existing entry
            currentEntry.setTitle(title);
            currentEntry.setContent(content);
            currentEntry.setTags(tags);
            currentEntry.setUpdatedAt(System.currentTimeMillis());

            journalViewModel.updateJournalEntry(currentEntry);
            binding.progressBar.setVisibility(View.GONE);
            binding.buttonSaveEntry.setEnabled(true);

            Toast.makeText(this, R.string.entry_updated, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_entry)
                .setMessage(R.string.delete_entry_confirmation)
                .setPositiveButton(R.string.delete, (dialog, which) -> deleteJournalEntry())
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void deleteJournalEntry() {
        if (currentEntry != null) {
            binding.progressBar.setVisibility(View.VISIBLE);
            journalViewModel.deleteJournalEntry(currentEntry);
            binding.progressBar.setVisibility(View.GONE);

            Toast.makeText(this, R.string.entry_deleted, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_journal_entry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_save) {
            saveJournalEntry();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
