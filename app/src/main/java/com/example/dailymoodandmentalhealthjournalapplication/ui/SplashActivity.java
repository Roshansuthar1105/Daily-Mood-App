package com.example.dailymoodandmentalhealthjournalapplication.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dailymoodandmentalhealthjournalapplication.R;
import com.example.dailymoodandmentalhealthjournalapplication.auth.LocalAuthManager;
import com.example.dailymoodandmentalhealthjournalapplication.databinding.ActivitySplashBinding;
import com.example.dailymoodandmentalhealthjournalapplication.ui.auth.LoginActivity;
import com.example.dailymoodandmentalhealthjournalapplication.utils.ThemeManager;

/**
 * Splash screen activity shown when the app starts.
 */
public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DURATION = 2000; // 2 seconds
    private ActivitySplashBinding binding;
    private LocalAuthManager authManager;
    private ThemeManager themeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize theme manager and apply theme
        themeManager = new ThemeManager(this);
        themeManager.applyTheme();

        // Initialize auth manager
        authManager = LocalAuthManager.getInstance(this);

        // Set up view binding
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Delay for splash screen
        new Handler(Looper.getMainLooper()).postDelayed(this::checkUserAndNavigate, SPLASH_DURATION);
    }

    /**
     * Check if user is logged in and navigate to the appropriate screen.
     */
    private void checkUserAndNavigate() {
        if (authManager.isUserLoggedIn()) {
            // User is logged in, go to main activity
            startActivity(new Intent(this, MainActivity.class));
        } else {
            // User is not logged in, go to login activity
            startActivity(new Intent(this, LoginActivity.class));
        }

        // Finish this activity so user can't go back to splash screen
        finish();
    }
}
