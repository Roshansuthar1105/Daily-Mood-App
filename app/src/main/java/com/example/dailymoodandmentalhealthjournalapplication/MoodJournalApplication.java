package com.example.dailymoodandmentalhealthjournalapplication;

import android.app.Application;
import android.util.Log;

import com.example.dailymoodandmentalhealthjournalapplication.data.database.AppDatabase;

/**
 * Custom Application class for initializing app-wide components.
 */
public class MoodJournalApplication extends Application {
    private static final String TAG = "MoodJournalApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize the database
        initializeDatabase();
    }

    /**
     * Initialize the Room database.
     */
    private void initializeDatabase() {
        try {
            Log.d(TAG, "Initializing database...");
            // This will create the database if it doesn't exist
            AppDatabase.getInstance(this);
            Log.d(TAG, "Database initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing database", e);
        }
    }
}
