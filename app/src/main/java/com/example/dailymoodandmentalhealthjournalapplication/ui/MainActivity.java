package com.example.dailymoodandmentalhealthjournalapplication.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.dailymoodandmentalhealthjournalapplication.R;
import com.example.dailymoodandmentalhealthjournalapplication.auth.LocalAuthManager;
import com.example.dailymoodandmentalhealthjournalapplication.databinding.ActivityMainBinding;
import com.example.dailymoodandmentalhealthjournalapplication.ui.analytics.AnalyticsActivity;
import com.example.dailymoodandmentalhealthjournalapplication.ui.auth.LoginActivity;
import com.example.dailymoodandmentalhealthjournalapplication.ui.fragments.DashboardFragment;
import com.example.dailymoodandmentalhealthjournalapplication.ui.fragments.JournalFragment;
import com.example.dailymoodandmentalhealthjournalapplication.ui.fragments.ProfileFragment;
import com.example.dailymoodandmentalhealthjournalapplication.ui.mood.MoodSelectionActivity;
import com.example.dailymoodandmentalhealthjournalapplication.ui.settings.SettingsActivity;
import com.example.dailymoodandmentalhealthjournalapplication.ui.viewmodels.UserViewModel;
import com.example.dailymoodandmentalhealthjournalapplication.utils.ThemeManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

/**
 * Main activity of the application.
 * Contains the bottom navigation and hosts the main fragments.
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private LocalAuthManager authManager;
    private UserViewModel userViewModel;
    private ThemeManager themeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize managers and view models
        authManager = LocalAuthManager.getInstance(this);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        themeManager = new ThemeManager(this);
        themeManager.applyTheme();

        // Check if user is logged in
        if (!authManager.isUserLoggedIn()) {
            // User is not logged in, go to login activity
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Set up view binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up toolbar
        setSupportActionBar(binding.toolbar);

        // Set up bottom navigation
        setupBottomNavigation();

        // Set up floating action button
        setupFloatingActionButton();

        // Load default fragment
        loadFragment(new DashboardFragment());
    }

    private void setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_dashboard) {
                fragment = new DashboardFragment();
            } else if (itemId == R.id.navigation_journal) {
                fragment = new JournalFragment();
            } else if (itemId == R.id.navigation_profile) {
                fragment = new ProfileFragment();
            }

            return loadFragment(fragment);
        });
    }

    private void setupFloatingActionButton() {
        binding.fab.setOnClickListener(view -> {
            // Open mood selection activity
            startActivity(new Intent(MainActivity.this, MoodSelectionActivity.class));
        });
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            // Open settings activity
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_analytics) {
            // Open analytics activity
            startActivity(new Intent(this, AnalyticsActivity.class));
            return true;
        } else if (id == R.id.action_logout) {
            // Log out user
            authManager.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
