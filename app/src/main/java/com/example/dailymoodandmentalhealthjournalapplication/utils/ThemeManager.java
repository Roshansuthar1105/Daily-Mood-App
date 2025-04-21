package com.example.dailymoodandmentalhealthjournalapplication.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * Utility class for managing app theme (dark/light mode).
 */
public class ThemeManager {
    private static final String PREFS_NAME = "theme_prefs";
    private static final String KEY_THEME_MODE = "theme_mode";

    public static final int MODE_SYSTEM = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
    public static final int MODE_LIGHT = AppCompatDelegate.MODE_NIGHT_NO;
    public static final int MODE_DARK = AppCompatDelegate.MODE_NIGHT_YES;

    private final SharedPreferences preferences;

    public ThemeManager(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Set the app theme mode.
     *
     * @param themeMode The theme mode to set (MODE_SYSTEM, MODE_LIGHT, or MODE_DARK)
     */
    public void setThemeMode(int themeMode) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_THEME_MODE, themeMode);
        editor.apply();
        
        AppCompatDelegate.setDefaultNightMode(themeMode);
    }

    /**
     * Get the current theme mode.
     *
     * @return The current theme mode
     */
    public int getThemeMode() {
        return preferences.getInt(KEY_THEME_MODE, getDefaultThemeMode());
    }

    /**
     * Apply the saved theme mode.
     */
    public void applyTheme() {
        int themeMode = getThemeMode();
        AppCompatDelegate.setDefaultNightMode(themeMode);
    }

    /**
     * Get the default theme mode based on the device's version.
     *
     * @return The default theme mode
     */
    private int getDefaultThemeMode() {
        return MODE_SYSTEM;
    }

    /**
     * Check if dark mode is currently active.
     *
     * @return True if dark mode is active, false otherwise
     */
    public boolean isDarkModeActive() {
        int currentMode = getThemeMode();
        return currentMode == MODE_DARK || 
               (currentMode == MODE_SYSTEM && 
                AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);
    }
}
