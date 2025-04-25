package com.example.dailymoodandmentalhealthjournalapplication.data.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.dailymoodandmentalhealthjournalapplication.data.database.AppDatabase;
import com.example.dailymoodandmentalhealthjournalapplication.data.entity.MoodEntry;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Repository class that handles data operations for MoodEntry.
 * Acts as a clean API for data access to the rest of the application.
 */
public class MoodRepository {
    private static final String TAG = "MoodRepository";
    private final AppDatabase database;
    private final Executor executor;

    public MoodRepository(Application application) {
        database = AppDatabase.getInstance(application);
        executor = Executors.newSingleThreadExecutor();
    }

    /**
     * Insert a new mood entry into the database.
     *
     * @param moodEntry The mood entry to insert
     * @param callback Callback to notify when operation is complete
     */
    public void insertMoodEntry(MoodEntry moodEntry, InsertCallback callback) {
        executor.execute(() -> {
            try {
                Log.d(TAG, "Inserting mood entry");
                long id = database.moodEntryDao().insert(moodEntry);
                Log.d(TAG, "Mood entry inserted with ID: " + id);
                callback.onInsertComplete(id);
            } catch (Exception e) {
                Log.e(TAG, "Error inserting mood entry", e);
                callback.onInsertComplete(-1);
            }
        });
    }

    /**
     * Update an existing mood entry.
     *
     * @param moodEntry The mood entry to update
     */
    public void updateMoodEntry(MoodEntry moodEntry) {
        executor.execute(() -> {
            try {
                database.moodEntryDao().update(moodEntry);
                Log.d(TAG, "Mood entry updated: " + moodEntry.getId());
            } catch (Exception e) {
                Log.e(TAG, "Error updating mood entry", e);
            }
        });
    }

    /**
     * Delete a mood entry.
     *
     * @param moodEntry The mood entry to delete
     */
    public void deleteMoodEntry(MoodEntry moodEntry) {
        executor.execute(() -> {
            try {
                database.moodEntryDao().delete(moodEntry);
                Log.d(TAG, "Mood entry deleted: " + moodEntry.getId());
            } catch (Exception e) {
                Log.e(TAG, "Error deleting mood entry", e);
            }
        });
    }

    /**
     * Get a mood entry by ID.
     *
     * @param id The ID of the mood entry
     * @return LiveData containing the mood entry
     */
    public LiveData<MoodEntry> getMoodEntryById(long id) {
        return database.moodEntryDao().getMoodEntryById(id);
    }

    /**
     * Get all mood entries for a user.
     *
     * @param userId The user ID
     * @return LiveData containing a list of mood entries
     */
    public LiveData<List<MoodEntry>> getAllMoodEntriesByUser(String userId) {
        return database.moodEntryDao().getAllMoodEntriesByUser(userId);
    }

    /**
     * Get mood entries for a user within a date range.
     *
     * @param userId The user ID
     * @param startDate The start date (in milliseconds)
     * @param endDate The end date (in milliseconds)
     * @return LiveData containing a list of mood entries
     */
    public LiveData<List<MoodEntry>> getMoodEntriesByDateRange(String userId, long startDate, long endDate) {
        return database.moodEntryDao().getMoodEntriesByDateRange(userId, startDate, endDate);
    }

    /**
     * Get mood entries for a user by mood type.
     *
     * @param userId The user ID
     * @param moodType The mood type
     * @return LiveData containing a list of mood entries
     */
    public LiveData<List<MoodEntry>> getMoodEntriesByType(String userId, String moodType) {
        return database.moodEntryDao().getMoodEntriesByType(userId, moodType);
    }

    /**
     * Get the count of mood entries for a user by mood type.
     *
     * @param userId The user ID
     * @param moodType The mood type
     * @return LiveData containing the count
     */
    public LiveData<Integer> countMoodEntriesByType(String userId, String moodType) {
        return database.moodEntryDao().countMoodEntriesByType(userId, moodType);
    }

    /**
     * Get the average mood intensity for a user within a date range.
     *
     * @param userId The user ID
     * @param startDate The start date (in milliseconds)
     * @param endDate The end date (in milliseconds)
     * @return LiveData containing the average mood intensity
     */
    public LiveData<Float> getAverageMoodIntensity(String userId, long startDate, long endDate) {
        return database.moodEntryDao().getAverageMoodIntensity(userId, startDate, endDate);
    }

    /**
     * Delete all mood entries for a user.
     *
     * @param userId The user ID
     */
    public void deleteAllMoodEntriesByUser(String userId) {
        executor.execute(() -> {
            try {
                database.moodEntryDao().deleteAllMoodEntriesByUser(userId);
                Log.d(TAG, "All mood entries deleted for user: " + userId);
            } catch (Exception e) {
                Log.e(TAG, "Error deleting all mood entries for user", e);
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
