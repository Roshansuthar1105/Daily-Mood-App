package com.example.dailymoodandmentalhealthjournalapplication.ui.viewmodels;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.dailymoodandmentalhealthjournalapplication.auth.LocalAuthManager;
import com.example.dailymoodandmentalhealthjournalapplication.data.entity.MoodEntry;
import com.example.dailymoodandmentalhealthjournalapplication.data.repository.MoodRepository;

import java.util.Calendar;
import java.util.List;

/**
 * ViewModel for mood-related operations.
 */
public class MoodViewModel extends AndroidViewModel {
    private final MoodRepository moodRepository;
    private final LocalAuthManager authManager;

    public MoodViewModel(@NonNull Application application) {
        super(application);
        moodRepository = new MoodRepository(application);
        authManager = LocalAuthManager.getInstance(application);
    }

/**
 * Add a new mood entry.
 *
 * @param moodType      The type of mood
 * @param moodIntensity The intensity of the mood (1-10)
 * @param notes         Additional notes
 * @param listener      Callback to notify when the operation completes
 */
public void addMoodEntry(String moodType, int moodIntensity, String notes, OnMoodAddedListener listener) {
    String userId = authManager.getCurrentUserId();
    android.util.Log.d("MoodViewModel", "Adding mood entry for user: " + userId);

    if (userId == null) {
        android.util.Log.e("MoodViewModel", "User ID is null, cannot add mood entry");
        listener.onMoodAdded(-1);
        return;
    }

    MoodEntry moodEntry = new MoodEntry(userId, System.currentTimeMillis(), moodType, moodIntensity);
    moodEntry.setNotes(notes);

    android.util.Log.d("MoodViewModel", "Created mood entry: " + moodType + ", intensity: " + moodIntensity);

    moodRepository.insertMoodEntry(moodEntry, id -> {
        // Run on main thread to update UI
        new Handler(Looper.getMainLooper()).post(() -> {
            android.util.Log.d("MoodViewModel", "Notifying listener of insert result: " + id);
            listener.onMoodAdded(id);
        });
    });
}

/**
 * Interface for mood entry addition callback.
 */
public interface OnMoodAddedListener {
    void onMoodAdded(long id);
}
    /**
     * Update an existing mood entry.
     *
     * @param moodEntry The updated mood entry
     */
    public void updateMoodEntry(MoodEntry moodEntry) {
        moodEntry.setUpdatedAt(System.currentTimeMillis());
        moodRepository.updateMoodEntry(moodEntry);
    }

    /**
     * Delete a mood entry.
     *
     * @param moodEntry The mood entry to delete
     */
    public void deleteMoodEntry(MoodEntry moodEntry) {
        moodRepository.deleteMoodEntry(moodEntry);
    }

    /**
     * Get a mood entry by ID.
     *
     * @param id The ID of the mood entry
     * @return LiveData containing the mood entry
     */
    public LiveData<MoodEntry> getMoodEntryById(long id) {
        return moodRepository.getMoodEntryById(id);
    }

    /**
     * Get all mood entries for the current user.
     *
     * @return LiveData containing a list of all mood entries
     */
    public LiveData<List<MoodEntry>> getAllMoodEntries() {
        String userId = authManager.getCurrentUserId();
        if (userId != null) {
            return moodRepository.getAllMoodEntriesByUser(userId);
        }
        return null;
    }

    /**
     * Get mood entries for a specific date range.
     *
     * @param startDate The start date (in milliseconds)
     * @param endDate   The end date (in milliseconds)
     * @return LiveData containing a list of mood entries in the date range
     */
    public LiveData<List<MoodEntry>> getMoodEntriesByDateRange(long startDate, long endDate) {
        String userId = authManager.getCurrentUserId();
        if (userId != null) {
            return moodRepository.getMoodEntriesByDateRange(userId, startDate, endDate);
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
            return moodRepository.getMoodEntriesByType(userId, moodType);
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
            return moodRepository.countMoodEntriesByType(userId, moodType);
        }
        return null;
    }

    /**
     * Get the average mood intensity for a date range.
     *
     * @param startDate The start date (in milliseconds)
     * @param endDate   The end date (in milliseconds)
     * @return LiveData containing the average mood intensity
     */
    public LiveData<Float> getAverageMoodIntensity(long startDate, long endDate) {
        String userId = authManager.getCurrentUserId();
        if (userId != null) {
            return moodRepository.getAverageMoodIntensity(userId, startDate, endDate);
        }
        return null;
    }
}
