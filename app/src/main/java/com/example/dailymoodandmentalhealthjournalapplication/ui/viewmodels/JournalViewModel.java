package com.example.dailymoodandmentalhealthjournalapplication.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.dailymoodandmentalhealthjournalapplication.auth.LocalAuthManager;
import com.example.dailymoodandmentalhealthjournalapplication.data.database.AppDatabase;
import com.example.dailymoodandmentalhealthjournalapplication.data.entity.JournalEntry;
import com.example.dailymoodandmentalhealthjournalapplication.utils.SentimentAnalyzer;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * ViewModel for journal-related operations.
 */
public class JournalViewModel extends AndroidViewModel {
    private final AppDatabase database;
    private final LocalAuthManager authManager;
    private final Executor executor;

    public JournalViewModel(@NonNull Application application) {
        super(application);
        database = AppDatabase.getInstance(application);
        authManager = LocalAuthManager.getInstance(application);
        executor = Executors.newSingleThreadExecutor();
    }

    /**
     * Add a new journal entry.
     *
     * @param title The title of the journal entry
     * @param content The content of the journal entry
     * @param tags The tags for the journal entry (comma-separated)
     * @return The ID of the new journal entry, or -1 if the operation failed
     */
    public long addJournalEntry(String title, String content, String tags) {
        String userId = authManager.getCurrentUserId();
        if (userId == null) {
            return -1;
        }

        final long[] id = {-1};

        JournalEntry journalEntry = new JournalEntry(userId, System.currentTimeMillis(), title, content);
        journalEntry.setTags(tags);

        executor.execute(() -> {
            id[0] = database.journalEntryDao().insert(journalEntry);
        });

        return id[0];
    }

    /**
     * Update an existing journal entry.
     *
     * @param journalEntry The updated journal entry
     */
    public void updateJournalEntry(JournalEntry journalEntry) {
        journalEntry.setUpdatedAt(System.currentTimeMillis());
        executor.execute(() -> database.journalEntryDao().update(journalEntry));
    }

    /**
     * Delete a journal entry.
     *
     * @param journalEntry The journal entry to delete
     */
    public void deleteJournalEntry(JournalEntry journalEntry) {
        executor.execute(() -> database.journalEntryDao().delete(journalEntry));
    }

    /**
     * Get a journal entry by ID.
     *
     * @param id The ID of the journal entry
     * @return LiveData containing the journal entry
     */
    public LiveData<JournalEntry> getJournalEntryById(long id) {
        return database.journalEntryDao().getJournalEntryById(id);
    }

    /**
     * Get all journal entries for the current user.
     *
     * @return LiveData containing a list of all journal entries
     */
    public LiveData<List<JournalEntry>> getAllJournalEntries() {
        String userId = authManager.getCurrentUserId();
        if (userId != null) {
            return database.journalEntryDao().getAllJournalEntriesByUser(userId);
        }
        return null;
    }

    /**
     * Get journal entries for a specific date range.
     *
     * @param startDate The start date (in milliseconds)
     * @param endDate The end date (in milliseconds)
     * @return LiveData containing a list of journal entries in the date range
     */
    public LiveData<List<JournalEntry>> getJournalEntriesByDateRange(long startDate, long endDate) {
        String userId = authManager.getCurrentUserId();
        if (userId != null) {
            return database.journalEntryDao().getJournalEntriesByDateRange(userId, startDate, endDate);
        }
        return null;
    }

    /**
     * Get journal entries for the current week.
     *
     * @return LiveData containing a list of journal entries for the current week
     */
    public LiveData<List<JournalEntry>> getJournalEntriesForCurrentWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startOfWeek = calendar.getTimeInMillis();

        calendar.add(Calendar.DAY_OF_WEEK, 6);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        long endOfWeek = calendar.getTimeInMillis();

        return getJournalEntriesByDateRange(startOfWeek, endOfWeek);
    }

    /**
     * Get journal entries for the current month.
     *
     * @return LiveData containing a list of journal entries for the current month
     */
    public LiveData<List<JournalEntry>> getJournalEntriesForCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startOfMonth = calendar.getTimeInMillis();

        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        long endOfMonth = calendar.getTimeInMillis();

        return getJournalEntriesByDateRange(startOfMonth, endOfMonth);
    }

    /**
     * Search journal entries by query.
     *
     * @param query The search query
     * @return LiveData containing a list of journal entries matching the query
     */
    public LiveData<List<JournalEntry>> searchJournalEntries(String query) {
        String userId = authManager.getCurrentUserId();
        if (userId != null) {
            return database.journalEntryDao().searchJournalEntries(userId, query);
        }
        return null;
    }

    /**
     * Get journal entries by tag.
     *
     * @param tag The tag to search for
     * @return LiveData containing a list of journal entries with the specified tag
     */
    public LiveData<List<JournalEntry>> getJournalEntriesByTag(String tag) {
        String userId = authManager.getCurrentUserId();
        if (userId != null) {
            return database.journalEntryDao().getJournalEntriesByTag(userId, tag);
        }
        return null;
    }

    /**
     * Get favorite journal entries.
     *
     * @return LiveData containing a list of favorite journal entries
     */
    public LiveData<List<JournalEntry>> getFavoriteJournalEntries() {
        String userId = authManager.getCurrentUserId();
        if (userId != null) {
            return database.journalEntryDao().getFavoriteJournalEntries(userId);
        }
        return null;
    }

    /**
     * Update the favorite status of a journal entry.
     *
     * @param id The ID of the journal entry
     * @param isFavorite The new favorite status
     */
    public void updateFavoriteStatus(long id, boolean isFavorite) {
        executor.execute(() -> database.journalEntryDao().updateFavoriteStatus(id, isFavorite));
    }

    /**
     * Analyze the sentiment of a journal entry.
     *
     * @param content The content of the journal entry
     * @return The sentiment score (-1.0 to 1.0)
     */
    public float analyzeSentiment(String content) {
        return SentimentAnalyzer.analyzeSentiment(content);
    }

    /**
     * Get the emotions in a journal entry.
     *
     * @param content The content of the journal entry
     * @param limit The maximum number of emotions to return
     * @return A map of emotions and their frequencies
     */
    public Map<String, Integer> getEmotions(String content, int limit) {
        return SentimentAnalyzer.getEmotions(content, limit);
    }
}
