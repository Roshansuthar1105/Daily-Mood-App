package com.example.dailymoodandmentalhealthjournalapplication.data.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.dailymoodandmentalhealthjournalapplication.data.database.AppDatabase;
import com.example.dailymoodandmentalhealthjournalapplication.data.entity.JournalEntry;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Repository class that handles data operations for JournalEntry.
 * Acts as a clean API for data access to the rest of the application.
 */
public class JournalRepository {
    private static final String TAG = "JournalRepository";
    private final AppDatabase database;
    private final Executor executor;

    public JournalRepository(Application application) {
        database = AppDatabase.getInstance(application);
        executor = Executors.newSingleThreadExecutor();
    }

    /**
     * Insert a new journal entry into the database.
     *
     * @param journalEntry The journal entry to insert
     * @param callback Callback to notify when operation is complete
     */
    public void insertJournalEntry(JournalEntry journalEntry, InsertCallback callback) {
        executor.execute(() -> {
            try {
                Log.d(TAG, "Inserting journal entry");
                long id = database.journalEntryDao().insert(journalEntry);
                Log.d(TAG, "Journal entry inserted with ID: " + id);
                callback.onInsertComplete(id);
            } catch (Exception e) {
                Log.e(TAG, "Error inserting journal entry", e);
                callback.onInsertComplete(-1);
            }
        });
    }

    /**
     * Update an existing journal entry.
     *
     * @param journalEntry The journal entry to update
     */
    public void updateJournalEntry(JournalEntry journalEntry) {
        executor.execute(() -> {
            try {
                database.journalEntryDao().update(journalEntry);
                Log.d(TAG, "Journal entry updated: " + journalEntry.getId());
            } catch (Exception e) {
                Log.e(TAG, "Error updating journal entry", e);
            }
        });
    }

    /**
     * Delete a journal entry.
     *
     * @param journalEntry The journal entry to delete
     */
    public void deleteJournalEntry(JournalEntry journalEntry) {
        executor.execute(() -> {
            try {
                database.journalEntryDao().delete(journalEntry);
                Log.d(TAG, "Journal entry deleted: " + journalEntry.getId());
            } catch (Exception e) {
                Log.e(TAG, "Error deleting journal entry", e);
            }
        });
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
     * Get all journal entries for a user.
     *
     * @param userId The user ID
     * @return LiveData containing a list of journal entries
     */
    public LiveData<List<JournalEntry>> getAllJournalEntriesByUser(String userId) {
        return database.journalEntryDao().getAllJournalEntriesByUser(userId);
    }

    /**
     * Get journal entries for a user within a date range.
     *
     * @param userId The user ID
     * @param startDate The start date (in milliseconds)
     * @param endDate The end date (in milliseconds)
     * @return LiveData containing a list of journal entries
     */
    public LiveData<List<JournalEntry>> getJournalEntriesByDateRange(String userId, long startDate, long endDate) {
        return database.journalEntryDao().getJournalEntriesByDateRange(userId, startDate, endDate);
    }

    /**
     * Search journal entries for a user.
     *
     * @param userId The user ID
     * @param query The search query
     * @return LiveData containing a list of journal entries
     */
    public LiveData<List<JournalEntry>> searchJournalEntries(String userId, String query) {
        return database.journalEntryDao().searchJournalEntries(userId, query);
    }

    /**
     * Get journal entries for a user by tag.
     *
     * @param userId The user ID
     * @param tag The tag to search for
     * @return LiveData containing a list of journal entries
     */
    public LiveData<List<JournalEntry>> getJournalEntriesByTag(String userId, String tag) {
        return database.journalEntryDao().getJournalEntriesByTag(userId, tag);
    }

    /**
     * Get favorite journal entries for a user.
     *
     * @param userId The user ID
     * @return LiveData containing a list of journal entries
     */
    public LiveData<List<JournalEntry>> getFavoriteJournalEntries(String userId) {
        return database.journalEntryDao().getFavoriteJournalEntries(userId);
    }

    /**
     * Update the favorite status of a journal entry.
     *
     * @param id The ID of the journal entry
     * @param isFavorite The new favorite status
     */
    public void updateFavoriteStatus(long id, boolean isFavorite) {
        executor.execute(() -> {
            try {
                database.journalEntryDao().updateFavoriteStatus(id, isFavorite);
                Log.d(TAG, "Journal entry favorite status updated: " + id + ", isFavorite: " + isFavorite);
            } catch (Exception e) {
                Log.e(TAG, "Error updating journal entry favorite status", e);
            }
        });
    }

    /**
     * Delete all journal entries for a user.
     *
     * @param userId The user ID
     */
    public void deleteAllJournalEntriesByUser(String userId) {
        executor.execute(() -> {
            try {
                database.journalEntryDao().deleteAllJournalEntriesByUser(userId);
                Log.d(TAG, "All journal entries deleted for user: " + userId);
            } catch (Exception e) {
                Log.e(TAG, "Error deleting all journal entries for user", e);
            }
        });
    }

    /**
     * Callback interface for insert operations.
     */
    public interface InsertCallback {
        void onInsertComplete(long id);
    }
}
