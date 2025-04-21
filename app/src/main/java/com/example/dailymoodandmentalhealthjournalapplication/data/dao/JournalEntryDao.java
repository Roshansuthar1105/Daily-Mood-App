package com.example.dailymoodandmentalhealthjournalapplication.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.dailymoodandmentalhealthjournalapplication.data.entity.JournalEntry;

import java.util.List;

/**
 * Data Access Object for the JournalEntry entity.
 */
@Dao
public interface JournalEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(JournalEntry journalEntry);

    @Update
    void update(JournalEntry journalEntry);

    @Delete
    void delete(JournalEntry journalEntry);

    @Query("SELECT * FROM journal_entries WHERE id = :id")
    LiveData<JournalEntry> getJournalEntryById(long id);

    @Query("SELECT * FROM journal_entries WHERE userId = :userId ORDER BY date DESC")
    LiveData<List<JournalEntry>> getAllJournalEntriesByUser(String userId);

    @Query("SELECT * FROM journal_entries WHERE userId = :userId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    LiveData<List<JournalEntry>> getJournalEntriesByDateRange(String userId, long startDate, long endDate);

    @Query("SELECT * FROM journal_entries WHERE userId = :userId AND (title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%') ORDER BY date DESC")
    LiveData<List<JournalEntry>> searchJournalEntries(String userId, String query);

    @Query("SELECT * FROM journal_entries WHERE userId = :userId AND tags LIKE '%' || :tag || '%' ORDER BY date DESC")
    LiveData<List<JournalEntry>> getJournalEntriesByTag(String userId, String tag);

    @Query("SELECT * FROM journal_entries WHERE userId = :userId AND isFavorite = 1 ORDER BY date DESC")
    LiveData<List<JournalEntry>> getFavoriteJournalEntries(String userId);

    @Query("UPDATE journal_entries SET isFavorite = :isFavorite WHERE id = :id")
    void updateFavoriteStatus(long id, boolean isFavorite);

    @Query("DELETE FROM journal_entries WHERE id = :id")
    void deleteJournalEntryById(long id);

    @Query("DELETE FROM journal_entries WHERE userId = :userId")
    void deleteAllJournalEntriesByUser(String userId);
}
