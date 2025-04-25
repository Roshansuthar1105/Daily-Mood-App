package com.example.dailymoodandmentalhealthjournalapplication.data.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.dailymoodandmentalhealthjournalapplication.data.database.AppDatabase;
import com.example.dailymoodandmentalhealthjournalapplication.data.entity.User;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Repository class that handles data operations for User.
 * Acts as a clean API for data access to the rest of the application.
 */
public class UserRepository {
    private static final String TAG = "UserRepository";
    private final AppDatabase database;
    private final Executor executor;

    public UserRepository(Application application) {
        database = AppDatabase.getInstance(application);
        executor = Executors.newSingleThreadExecutor();
    }

    /**
     * Insert a new user into the database.
     *
     * @param user The user to insert
     */
    public void insertUser(User user) {
        executor.execute(() -> {
            try {
                Log.d(TAG, "Inserting user: " + user.getUserId());
                database.userDao().insert(user);
                Log.d(TAG, "User inserted: " + user.getUserId());
            } catch (Exception e) {
                Log.e(TAG, "Error inserting user", e);
            }
        });
    }

    /**
     * Update an existing user.
     *
     * @param user The user to update
     */
    public void updateUser(User user) {
        executor.execute(() -> {
            try {
                database.userDao().update(user);
                Log.d(TAG, "User updated: " + user.getUserId());
            } catch (Exception e) {
                Log.e(TAG, "Error updating user", e);
            }
        });
    }

    /**
     * Delete a user.
     *
     * @param user The user to delete
     */
    public void deleteUser(User user) {
        executor.execute(() -> {
            try {
                database.userDao().delete(user);
                Log.d(TAG, "User deleted: " + user.getUserId());
            } catch (Exception e) {
                Log.e(TAG, "Error deleting user", e);
            }
        });
    }

    /**
     * Get a user by ID.
     *
     * @param userId The user ID
     * @return LiveData containing the user
     */
    public LiveData<User> getUserById(String userId) {
        return database.userDao().getUserById(userId);
    }

    /**
     * Get a user by ID synchronously.
     *
     * @param userId The user ID
     * @return The user
     */
    public User getUserByIdSync(String userId) {
        return database.userDao().getUserByIdSync(userId);
    }

    /**
     * Get a user by email.
     *
     * @param email The user's email
     * @return LiveData containing the user
     */
    public LiveData<User> getUserByEmail(String email) {
        return database.userDao().getUserByEmail(email);
    }

    /**
     * Get a user by email synchronously.
     *
     * @param email The user's email
     * @return The user
     */
    public User getUserByEmailSync(String email) {
        return database.userDao().getUserByEmailSync(email);
    }

    /**
     * Delete a user by ID.
     *
     * @param userId The user ID
     */
    public void deleteUserById(String userId) {
        executor.execute(() -> {
            try {
                database.userDao().deleteUserById(userId);
                Log.d(TAG, "User deleted by ID: " + userId);
            } catch (Exception e) {
                Log.e(TAG, "Error deleting user by ID", e);
            }
        });
    }
}
