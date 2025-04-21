package com.example.dailymoodandmentalhealthjournalapplication.auth;

import android.content.Context;
import android.content.Intent;

import com.example.dailymoodandmentalhealthjournalapplication.data.entity.User;

/**
 * Compatibility wrapper for LocalAuthManager.
 * This class delegates all operations to LocalAuthManager to maintain compatibility
 * with existing code while removing Firebase dependencies.
 */
public class AuthManager {
    private static volatile AuthManager INSTANCE;
    private final LocalAuthManager localAuthManager;

    private AuthManager(Context context) {
        localAuthManager = LocalAuthManager.getInstance(context);
    }

    public static AuthManager getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AuthManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AuthManager(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    public FirebaseUserWrapper getCurrentUser() {
        // Return a dummy object that has a getUid() method
        String userId = localAuthManager.getCurrentUserId();
        if (userId == null) {
            return null;
        }

        return new FirebaseUserWrapper(userId);
    }

    public boolean isUserLoggedIn() {
        return localAuthManager.isUserLoggedIn();
    }

    public void signOut() {
        localAuthManager.signOut();
    }

    public void saveUserToDatabase(final User user) {
        localAuthManager.saveUserToDatabase(user);
    }

    public void updateUserLastLogin(final String userId) {
        localAuthManager.updateUserLastLogin(userId);
    }

    // Add stub methods for other required functionality
    public void registerUser(String email, String password, Object listener) {
        // Not implemented - using LocalAuthManager directly is recommended
    }

    public void loginUser(String email, String password, Object listener) {
        // Not implemented - using LocalAuthManager directly is recommended
    }

    public void resetPassword(String email, Object listener) {
        // Not implemented - using LocalAuthManager directly is recommended
    }

    public Intent getGoogleSignInIntent() {
        // Not implemented - Google Sign-In is not available in the local version
        return new Intent();
    }

    public void handleGoogleSignInResult(Intent data, Object listener) {
        // Not implemented - Google Sign-In is not available in the local version
    }

    /**
     * Simple wrapper class to mimic FirebaseUser functionality
     */
    public static class FirebaseUserWrapper {
        private final String userId;

        public FirebaseUserWrapper(String userId) {
            this.userId = userId;
        }

        public String getUid() {
            return userId;
        }
    }
}