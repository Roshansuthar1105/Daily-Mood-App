package com.example.dailymoodandmentalhealthjournalapplication.ui.settings;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.dailymoodandmentalhealthjournalapplication.R;
import com.example.dailymoodandmentalhealthjournalapplication.databinding.ActivitySettingsBinding;
import com.example.dailymoodandmentalhealthjournalapplication.utils.NotificationManager;
import com.example.dailymoodandmentalhealthjournalapplication.utils.ThemeManager;

/**
 * Activity for managing app settings.
 */
public class SettingsActivity extends AppCompatActivity {
    private ActivitySettingsBinding binding;
    private ThemeManager themeManager;
    private NotificationManager notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set up view binding
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Set up toolbar
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.action_settings);
        
        // Initialize managers
        themeManager = new ThemeManager(this);
        notificationManager = new NotificationManager(this);
        
        // Set up theme settings
        setupThemeSettings();
        
        // Set up notification settings
        setupNotificationSettings();
        
        // Set up data backup settings
        setupDataBackupSettings();
        
        // Set up about section
        setupAboutSection();
    }

    private void setupThemeSettings() {
        // Set the current theme selection
        int currentTheme = themeManager.getThemeMode();
        if (currentTheme == ThemeManager.MODE_LIGHT) {
            binding.radioLight.setChecked(true);
        } else if (currentTheme == ThemeManager.MODE_DARK) {
            binding.radioDark.setChecked(true);
        } else {
            binding.radioSystem.setChecked(true);
        }
        
        // Set up radio button listeners
        binding.radioGroupTheme.setOnCheckedChangeListener((group, checkedId) -> {
            int themeMode;
            if (checkedId == R.id.radioLight) {
                themeMode = ThemeManager.MODE_LIGHT;
            } else if (checkedId == R.id.radioDark) {
                themeMode = ThemeManager.MODE_DARK;
            } else {
                themeMode = ThemeManager.MODE_SYSTEM;
            }
            
            // Apply the selected theme
            themeManager.setThemeMode(themeMode);
            recreate();
        });
    }

    private void setupNotificationSettings() {
        // Set up daily reminder switch
        binding.switchDailyReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Schedule daily reminder
                notificationManager.scheduleReminder(20, 0); // Default to 8:00 PM
                Toast.makeText(this, R.string.reminder_enabled, Toast.LENGTH_SHORT).show();
            } else {
                // Cancel daily reminder
                notificationManager.cancelReminders();
                Toast.makeText(this, R.string.reminder_disabled, Toast.LENGTH_SHORT).show();
            }
        });
        
        // Set up reminder time picker
        binding.buttonReminderTime.setOnClickListener(v -> {
            // Show time picker dialog
            // This would be implemented with a TimePickerDialog
            Toast.makeText(this, R.string.feature_not_implemented, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupDataBackupSettings() {
        // Set up backup to cloud button
        binding.buttonBackupToCloud.setOnClickListener(v -> {
            // Backup data to cloud
            // This would be implemented with Firebase Firestore
            Toast.makeText(this, R.string.feature_not_implemented, Toast.LENGTH_SHORT).show();
        });
        
        // Set up restore from cloud button
        binding.buttonRestoreFromCloud.setOnClickListener(v -> {
            // Restore data from cloud
            // This would be implemented with Firebase Firestore
            Toast.makeText(this, R.string.feature_not_implemented, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupAboutSection() {
        // Set up version text
        binding.textViewVersionValue.setText(getVersionName());
        
        // Set up privacy policy button
        binding.buttonPrivacyPolicy.setOnClickListener(v -> {
            // Open privacy policy
            // This would be implemented with a WebView or browser intent
            Toast.makeText(this, R.string.feature_not_implemented, Toast.LENGTH_SHORT).show();
        });
        
        // Set up terms of service button
        binding.buttonTermsOfService.setOnClickListener(v -> {
            // Open terms of service
            // This would be implemented with a WebView or browser intent
            Toast.makeText(this, R.string.feature_not_implemented, Toast.LENGTH_SHORT).show();
        });
    }

    private String getVersionName() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception e) {
            return "1.0.0";
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
