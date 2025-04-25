package com.example.dailymoodandmentalhealthjournalapplication.ui.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.dailymoodandmentalhealthjournalapplication.auth.LocalAuthManager;
import com.example.dailymoodandmentalhealthjournalapplication.data.entity.User;
import com.example.dailymoodandmentalhealthjournalapplication.data.repository.JournalRepository;
import com.example.dailymoodandmentalhealthjournalapplication.data.repository.MoodRepository;
import com.example.dailymoodandmentalhealthjournalapplication.data.repository.UserRepository;

/**
 * ViewModel for user-related operations.
 */
public class UserViewModel extends AndroidViewModel {
    private final UserRepository userRepository;
    private final MoodRepository moodRepository;
    private final JournalRepository journalRepository;
    private final LocalAuthManager authManager;

    public UserViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
        moodRepository = new MoodRepository(application);
        journalRepository = new JournalRepository(application);
        authManager = LocalAuthManager.getInstance(application);
    }

    /**
     * Get the current user from the database.
     *
     * @return LiveData containing the current user
     */
    public LiveData<User> getCurrentUser() {
        String userId = authManager.getCurrentUserId();
        if (userId != null) {
            return userRepository.getUserById(userId);
        }
        return null;
    }

    /**
     * Update user profile information.
     *
     * @param user The updated user object
     */
    public void updateUser(User user) {
        userRepository.updateUser(user);
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
            User user = userRepository.getUserByIdSync(userId);
            if (user != null) {
                user.setName(name);
                user.setAge(age);
                user.setGender(gender);
                user.setProfilePictureUrl(profilePictureUrl);
                user.setUpdatedAt(System.currentTimeMillis());
                userRepository.updateUser(user);
            }
        }
    }

    /**
     * Update user's profile picture URL.
     *
     * @param userId The user ID
     * @param profilePictureUrl The URL of the profile picture
     */
    public void updateProfilePicture(String userId, String profilePictureUrl) {
        LiveData<User> userLiveData = userRepository.getUserById(userId);
        User user = userLiveData.getValue();
        if (user != null) {
            user.setProfilePictureUrl(profilePictureUrl);
            userRepository.updateUser(user);
        }
    }

    /**
     * Delete the current user's account.
     */
    public void deleteAccount() {
        String userId = authManager.getCurrentUserId();
        if (userId != null) {
            // Delete user from database
            userRepository.deleteUserById(userId);

            // Delete all user data
            moodRepository.deleteAllMoodEntriesByUser(userId);
            journalRepository.deleteAllJournalEntriesByUser(userId);

            // Sign out
            authManager.signOut();
        }
    }
}
