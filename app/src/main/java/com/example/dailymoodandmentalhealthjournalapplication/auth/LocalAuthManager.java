package com.example.dailymoodandmentalhealthjournalapplication.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.dailymoodandmentalhealthjournalapplication.data.database.AppDatabase;
import com.example.dailymoodandmentalhealthjournalapplication.data.entity.User;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Manager class for local authentication operations.
 */
public class LocalAuthManager {
    private static final String TAG = "LocalAuthManager";
    private static final String PREF_NAME = "auth_prefs";
    private static final String KEY_CURRENT_USER_ID = "current_user_id";
    private static final String KEY_USER_PREFIX = "user_";
    
    private static LocalAuthManager instance;
    
    private final Context context;
    private final SharedPreferences securePrefs;
    private final AppDatabase database;
    private final Executor executor;
    
    private LocalAuthManager(Context context) {
        this.context = context.getApplicationContext();
        this.database = AppDatabase.getInstance(context);
        this.executor = Executors.newSingleThreadExecutor();
        
        // Initialize encrypted shared preferences
        SharedPreferences prefs;
        try {
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();
            
            prefs = EncryptedSharedPreferences.create(
                    context,
                    PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            Log.e(TAG, "Error creating encrypted shared preferences", e);
            // Fall back to regular shared preferences if encryption fails
            prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
        
        this.securePrefs = prefs;
    }
    
    /**
     * Get the singleton instance of LocalAuthManager.
     *
     * @param context The context
     * @return The LocalAuthManager instance
     */
    public static synchronized LocalAuthManager getInstance(Context context) {
        if (instance == null) {
            instance = new LocalAuthManager(context);
        }
        return instance;
    }
    
    /**
     * Check if a user is currently logged in.
     *
     * @return True if a user is logged in, false otherwise
     */
    public boolean isUserLoggedIn() {
        return securePrefs.contains(KEY_CURRENT_USER_ID);
    }
    
    /**
     * Get the current user ID.
     *
     * @return The current user ID, or null if no user is logged in
     */
    public String getCurrentUserId() {
        return securePrefs.getString(KEY_CURRENT_USER_ID, null);
    }
    
    /**
     * Register a new user with email and password.
     *
     * @param email The user's email
     * @param password The user's password
     * @param name The user's name
     * @param callback The callback to be called when the operation completes
     */
    public void registerUser(String email, String password, String name, AuthCallback callback) {
        executor.execute(() -> {
            try {
                // Check if email is already registered
                if (isEmailRegistered(email)) {
                    callback.onFailure("Email already registered");
                    return;
                }
                
                // Generate a unique user ID
                String userId = UUID.randomUUID().toString();
                
                // Hash the password
                String hashedPassword = hashPassword(password);
                
                // Save user credentials
                SharedPreferences.Editor editor = securePrefs.edit();
                editor.putString(KEY_USER_PREFIX + email, hashedPassword);
                editor.putString(KEY_CURRENT_USER_ID, userId);
                editor.apply();
                
                // Create user in database
                User user = new User(userId, name, email);
                database.userDao().insert(user);
                
                // Notify success
                callback.onSuccess(userId);
            } catch (Exception e) {
                Log.e(TAG, "Error registering user", e);
                callback.onFailure("Registration failed: " + e.getMessage());
            }
        });
    }
    
    /**
     * Log in a user with email and password.
     *
     * @param email The user's email
     * @param password The user's password
     * @param callback The callback to be called when the operation completes
     */
    public void loginUser(String email, String password, AuthCallback callback) {
        executor.execute(() -> {
            try {
                // Check if email is registered
                if (!isEmailRegistered(email)) {
                    callback.onFailure("Email not registered");
                    return;
                }
                
                // Get stored password hash
                String storedHash = securePrefs.getString(KEY_USER_PREFIX + email, null);
                
                // Hash the provided password
                String inputHash = hashPassword(password);
                
                // Compare password hashes
                if (!inputHash.equals(storedHash)) {
                    callback.onFailure("Invalid password");
                    return;
                }
                
                // Get user from database
                User user = database.userDao().getUserByEmailSync(email);
                if (user == null) {
                    callback.onFailure("User not found in database");
                    return;
                }
                
                // Set current user ID
                SharedPreferences.Editor editor = securePrefs.edit();
                editor.putString(KEY_CURRENT_USER_ID, user.getUserId());
                editor.apply();
                
                // Update last login time
                updateUserLastLogin(user.getUserId());
                
                // Notify success
                callback.onSuccess(user.getUserId());
            } catch (Exception e) {
                Log.e(TAG, "Error logging in user", e);
                callback.onFailure("Login failed: " + e.getMessage());
            }
        });
    }
    
    /**
     * Sign out the current user.
     */
    public void signOut() {
        SharedPreferences.Editor editor = securePrefs.edit();
        editor.remove(KEY_CURRENT_USER_ID);
        editor.apply();
    }
    
    /**
     * Delete the current user's account.
     *
     * @param callback The callback to be called when the operation completes
     */
    public void deleteAccount(AuthCallback callback) {
        String userId = getCurrentUserId();
        if (userId == null) {
            callback.onFailure("No user logged in");
            return;
        }
        
        executor.execute(() -> {
            try {
                // Get user from database
                User user = database.userDao().getUserByIdSync(userId);
                if (user == null) {
                    callback.onFailure("User not found in database");
                    return;
                }
                
                // Remove user credentials
                SharedPreferences.Editor editor = securePrefs.edit();
                editor.remove(KEY_USER_PREFIX + user.getEmail());
                editor.remove(KEY_CURRENT_USER_ID);
                editor.apply();
                
                // Delete user from database
                database.userDao().delete(user);
                
                // Delete all user data
                database.moodEntryDao().deleteAllMoodEntriesByUser(userId);
                database.journalEntryDao().deleteAllJournalEntriesByUser(userId);
                
                // Notify success
                callback.onSuccess(null);
            } catch (Exception e) {
                Log.e(TAG, "Error deleting account", e);
                callback.onFailure("Account deletion failed: " + e.getMessage());
            }
        });
    }
    
    /**
     * Save a user to the database.
     *
     * @param user The user to save
     */
    public void saveUserToDatabase(User user) {
        executor.execute(() -> database.userDao().insert(user));
    }
    
    /**
     * Update a user's last login time.
     *
     * @param userId The user's ID
     */
    public void updateUserLastLogin(String userId) {
        executor.execute(() -> database.userDao().updateLastLogin(userId, System.currentTimeMillis()));
    }
    
    /**
     * Check if an email is already registered.
     *
     * @param email The email to check
     * @return True if the email is registered, false otherwise
     */
    private boolean isEmailRegistered(String email) {
        return securePrefs.contains(KEY_USER_PREFIX + email);
    }
    
    /**
     * Hash a password using SHA-256.
     *
     * @param password The password to hash
     * @return The hashed password
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            
            // Convert byte array to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Error hashing password", e);
            // Fallback to simple string if hashing fails
            return password + "_hashed";
        }
    }
    
    /**
     * Callback interface for authentication operations.
     */
    public interface AuthCallback {
        void onSuccess(String userId);
        void onFailure(String errorMessage);
    }
}
