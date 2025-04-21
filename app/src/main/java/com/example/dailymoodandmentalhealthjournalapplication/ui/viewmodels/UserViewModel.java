package com.example.dailymoodandmentalhealthjournalapplication.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.dailymoodandmentalhealthjournalapplication.auth.LocalAuthManager;
import com.example.dailymoodandmentalhealthjournalapplication.data.database.AppDatabase;
import com.example.dailymoodandmentalhealthjournalapplication.data.entity.User;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * ViewModel for user-related operations.
 */
public class UserViewModel extends AndroidViewModel {
    private final AppDatabase database;
    private final LocalAuthManager authManager;
    private final Executor executor;

    public UserViewModel(@NonNull Application application) {
        super(application);
        database = AppDatabase.getInstance(application);
        authManager = LocalAuthManager.getInstance(application);
        executor = Executors.newSingleThreadExecutor();
    }

    /**
     * Get the current user from the database.
     *
     * @return LiveData containing the current user
     */
    public LiveData<User> getCurrentUser() {
        String userId = authManager.getCurrentUserId();
        if (userId != null) {
            return database.userDao().getUserById(userId);
        }
        return null;
    }

    /**
     * Update user profile information.
     *
     * @param user The updated user object
     */
    public void updateUser(User user) {
        executor.execute(() -> database.userDao().update(user));
    }

    /**
     * Update user profile information.
     *
     * @param name The user's name
     * @param age The user's age
     * @param gender The user's gender
     * @param profilePictureUrl The URL of the user's profile picture
     */
    public void updateUserProfile(String name, int age, String gender, String profilePictureUrl) {
        String userId = authManager.getCurrentUserId();
        if (userId != null) {
            executor.execute(() -> {
                User user = database.userDao().getUserByIdSync(userId);
                if (user != null) {
                    user.setName(name);
                    user.setAge(age);
                    user.setGender(gender);
                    user.setProfilePictureUrl(profilePictureUrl);
                    user.setUpdatedAt(System.currentTimeMillis());
                    database.userDao().update(user);
                }
            });
        }
    }

    /**
     * Update user's profile picture URL.
     *
     * @param userId The user ID
     * @param profilePictureUrl The URL of the profile picture
     */
    public void updateProfilePicture(String userId, String profilePictureUrl) {
        executor.execute(() -> {
            LiveData<User> userLiveData = database.userDao().getUserById(userId);
            User user = userLiveData.getValue();
            if (user != null) {
                user.setProfilePictureUrl(profilePictureUrl);
                database.userDao().update(user);
            }
        });
    }

    /**
     * Delete the current user's account.
     */
    public void deleteAccount() {
        String userId = authManager.getCurrentUserId();
        if (userId != null) {
            executor.execute(() -> {
                // Delete user from database
                database.userDao().deleteUserById(userId);

                // Delete all user data
                database.moodEntryDao().deleteAllMoodEntriesByUser(userId);
                database.journalEntryDao().deleteAllJournalEntriesByUser(userId);

                // Sign out
                authManager.signOut();
            });
        }
    }
}
