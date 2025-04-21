package com.example.dailymoodandmentalhealthjournalapplication.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.dailymoodandmentalhealthjournalapplication.data.dao.JournalEntryDao;
import com.example.dailymoodandmentalhealthjournalapplication.data.dao.MoodEntryDao;
import com.example.dailymoodandmentalhealthjournalapplication.data.dao.UserDao;
import com.example.dailymoodandmentalhealthjournalapplication.data.entity.JournalEntry;
import com.example.dailymoodandmentalhealthjournalapplication.data.entity.MoodEntry;
import com.example.dailymoodandmentalhealthjournalapplication.data.entity.User;

/**
 * Main database class for the application.
 */
@Database(entities = {User.class, MoodEntry.class, JournalEntry.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "mood_journal_db";
    private static volatile AppDatabase INSTANCE;

    // DAOs
    public abstract UserDao userDao();
    public abstract MoodEntryDao moodEntryDao();
    public abstract JournalEntryDao journalEntryDao();

    // Singleton pattern to get database instance
    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
