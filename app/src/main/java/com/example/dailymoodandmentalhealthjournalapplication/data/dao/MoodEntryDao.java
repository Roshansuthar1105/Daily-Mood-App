package com.example.dailymoodandmentalhealthjournalapplication.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.dailymoodandmentalhealthjournalapplication.data.entity.MoodEntry;

import java.util.List;

/**
 * Data Access Object for the MoodEntry entity.
 */
@Dao
public interface MoodEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(MoodEntry moodEntry);

    @Update
    void update(MoodEntry moodEntry);

    @Delete
    void delete(MoodEntry moodEntry);

    @Query("SELECT * FROM mood_entries WHERE id = :id")
    LiveData<MoodEntry> getMoodEntryById(long id);

    @Query("SELECT * FROM mood_entries WHERE userId = :userId ORDER BY date DESC")
    LiveData<List<MoodEntry>> getAllMoodEntriesByUser(String userId);

    @Query("SELECT * FROM mood_entries WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    LiveData<List<MoodEntry>> getMoodEntriesByDateRange(String userId, long startDate, long endDate);

    @Query("SELECT * FROM mood_entries WHERE userId = :userId AND moodType = :moodType ORDER BY date DESC")
    LiveData<List<MoodEntry>> getMoodEntriesByType(String userId, String moodType);

    @Query("SELECT COUNT(*) FROM mood_entries WHERE userId = :userId AND moodType = :moodType")
    LiveData<Integer> countMoodEntriesByType(String userId, String moodType);

    @Query("SELECT AVG(moodIntensity) FROM mood_entries WHERE userId = :userId AND date BETWEEN :startDate AND :endDate")
    LiveData<Float> getAverageMoodIntensity(String userId, long startDate, long endDate);

    @Query("DELETE FROM mood_entries WHERE id = :id")
    void deleteMoodEntryById(long id);

    @Query("DELETE FROM mood_entries WHERE userId = :userId")
    void deleteAllMoodEntriesByUser(String userId);
}
