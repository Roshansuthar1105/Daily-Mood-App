package com.example.dailymoodandmentalhealthjournalapplication.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.dailymoodandmentalhealthjournalapplication.auth.LocalAuthManager;
import com.example.dailymoodandmentalhealthjournalapplication.data.database.AppDatabase;
import com.example.dailymoodandmentalhealthjournalapplication.data.entity.MoodEntry;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * ViewModel for mood-related operations.
 */
public class MoodViewModel extends AndroidViewModel {
    private final AppDatabase database;
    private final LocalAuthManager authManager;
    private final Executor executor;

    public MoodViewModel(@NonNull Application application) {
        super(application);
        database = AppDatabase.getInstance(application);
        authManager = LocalAuthManager.getInstance(application);
        executor = Executors.newSingleThreadExecutor();
    }

    /**
     * Add a new mood entry.
     *
     * @param moodType The type of mood
     * @param moodIntensity The intensity of the mood (1-10)
     * @param notes Additional notes
     * @return The ID of the new mood entry, or -1 if the operation failed
     */
    public long addMoodEntry(String moodType, int moodIntensity, String notes) {
        String userId = authManager.getCurrentUserId();
        if (userId == null) {
            return -1;
        }

        final long[] id = {-1};

        MoodEntry moodEntry = new MoodEntry(userId, System.currentTimeMillis(), moodType, moodIntensity);
        moodEntry.setNotes(notes);

        executor.execute(() -> {
            id[0] = database.moodEntryDao().insert(moodEntry);
        });

        return id[0];
    }

    /**
     * Update an existing mood entry.
     *
     * @param moodEntry The updated mood entry
     */
    public void updateMoodEntry(MoodEntry moodEntry) {
        moodEntry.setUpdatedAt(System.currentTimeMillis());
        executor.execute(() -> database.moodEntryDao().update(moodEntry));
    }

    /**
     * Delete a mood entry.
     *
     * @param moodEntry The mood entry to delete
     */
    public void deleteMoodEntry(MoodEntry moodEntry) {
        executor.execute(() -> database.moodEntryDao().delete(moodEntry));
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
     * Get all mood entries for the current user.
     *
     * @return LiveData containing a list of all mood entries
     */
    public LiveData<List<MoodEntry>> getAllMoodEntries() {
        String userId = authManager.getCurrentUserId();
        if (userId != null) {
            return database.moodEntryDao().getAllMoodEntriesByUser(userId);
        }
        return null;
    }

    /**
     * Get mood entries for a specific date range.
     *
     * @param startDate The start date (in milliseconds)
     * @param endDate The end date (in milliseconds)
     * @return LiveData containing a list of mood entries in the date range
     */
    public LiveData<List<MoodEntry>> getMoodEntriesByDateRange(long startDate, long endDate) {
        String userId = authManager.getCurrentUserId();
        if (userId != null) {
            return database.moodEntryDao().getMoodEntriesByDateRange(userId, startDate, endDate);
        }
        return null;
    }

    /**
     * Get mood entries for the current week.
     *
     * @return LiveData containing a list of mood entries for the current week
     */
    public LiveData<List<MoodEntry>> getMoodEntriesForCurrentWeek() {
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

        return getMoodEntriesByDateRange(startOfWeek, endOfWeek);
    }

    /**
     * Get mood entries for the current month.
     *
     * @return LiveData containing a list of mood entries for the current month
     */
    public LiveData<List<MoodEntry>> getMoodEntriesForCurrentMonth() {
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

        return getMoodEntriesByDateRange(startOfMonth, endOfMonth);
    }

    /**
     * Get mood entries by type.
     *
     * @param moodType The type of mood
     * @return LiveData containing a list of mood entries of the specified type
     */
    public LiveData<List<MoodEntry>> getMoodEntriesByType(String moodType) {
        String userId = authManager.getCurrentUserId();
        if (userId != null) {
            return database.moodEntryDao().getMoodEntriesByType(userId, moodType);
        }
        return null;
    }

    /**
     * Get the count of mood entries by type.
     *
     * @param moodType The type of mood
     * @return LiveData containing the count of mood entries of the specified type
     */
    public LiveData<Integer> countMoodEntriesByType(String moodType) {
        String userId = authManager.getCurrentUserId();
        if (userId != null) {
            return database.moodEntryDao().countMoodEntriesByType(userId, moodType);
        }
        return null;
    }

    /**
     * Get the average mood intensity for a date range.
     *
     * @param startDate The start date (in milliseconds)
     * @param endDate The end date (in milliseconds)
     * @return LiveData containing the average mood intensity
     */
    public LiveData<Float> getAverageMoodIntensity(long startDate, long endDate) {
        String userId = authManager.getCurrentUserId();
        if (userId != null) {
            return database.moodEntryDao().getAverageMoodIntensity(userId, startDate, endDate);
        }
        return null;
    }
}
