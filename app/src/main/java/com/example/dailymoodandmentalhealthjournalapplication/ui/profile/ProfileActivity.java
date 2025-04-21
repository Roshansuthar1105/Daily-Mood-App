package com.example.dailymoodandmentalhealthjournalapplication.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.dailymoodandmentalhealthjournalapplication.R;
import com.example.dailymoodandmentalhealthjournalapplication.auth.LocalAuthManager;
import com.example.dailymoodandmentalhealthjournalapplication.databinding.ActivityProfileBinding;
import com.example.dailymoodandmentalhealthjournalapplication.ui.auth.LoginActivity;
import com.example.dailymoodandmentalhealthjournalapplication.ui.settings.SettingsActivity;
import com.example.dailymoodandmentalhealthjournalapplication.ui.viewmodels.UserViewModel;

/**
 * Activity for displaying user profile information.
 */
public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;
    private UserViewModel userViewModel;
    private LocalAuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set up view binding
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Set up toolbar
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.profile);
        
        // Initialize view model and auth manager
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        authManager = LocalAuthManager.getInstance(this);
        
        // Load user profile
        loadUserProfile();
        
        // Set up buttons
        setupButtons();
    }

    private void loadUserProfile() {
        binding.progressBar.setVisibility(View.VISIBLE);
        userViewModel.getCurrentUser().observe(this, user -> {
            binding.progressBar.setVisibility(View.GONE);
            if (user != null) {
                // Set user name
                binding.textViewName.setText(user.getName());
                
                // Set user email
                binding.textViewEmail.setText(user.getEmail());
                
                // Set user age and gender if available
                if (user.getAge() > 0) {
                    binding.textViewAge.setText(String.valueOf(user.getAge()));
                    binding.textViewAge.setVisibility(View.VISIBLE);
                    binding.textViewAgeLabel.setVisibility(View.VISIBLE);
                } else {
                    binding.textViewAge.setVisibility(View.GONE);
                    binding.textViewAgeLabel.setVisibility(View.GONE);
                }
                
                if (user.getGender() != null && !user.getGender().isEmpty()) {
                    binding.textViewGender.setText(user.getGender());
                    binding.textViewGender.setVisibility(View.VISIBLE);
                    binding.textViewGenderLabel.setVisibility(View.VISIBLE);
                } else {
                    binding.textViewGender.setVisibility(View.GONE);
                    binding.textViewGenderLabel.setVisibility(View.GONE);
                }
                
                // Load profile picture if available
                if (user.getProfilePictureUrl() != null && !user.getProfilePictureUrl().isEmpty()) {
                    Glide.with(this)
                            .load(user.getProfilePictureUrl())
                            .placeholder(R.drawable.ic_profile_placeholder)
                            .error(R.drawable.ic_profile_placeholder)
                            .circleCrop()
                            .into(binding.imageViewProfile);
                } else {
                    binding.imageViewProfile.setImageResource(R.drawable.ic_profile_placeholder);
                }
            }
        });
    }

    private void setupButtons() {
        // Edit profile button
        binding.buttonEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditProfileActivity.class);
            startActivity(intent);
        });
        
        // Settings button
        binding.buttonSettings.setOnClickListener(v -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });
        
        // Logout button
        binding.buttonLogout.setOnClickListener(v -> {
            authManager.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        
        // Delete account button
        binding.buttonDeleteAccount.setOnClickListener(v -> {
            showDeleteAccountConfirmationDialog();
        });
    }

    private void showDeleteAccountConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_account)
                .setMessage(R.string.delete_account_confirmation)
                .setPositiveButton(R.string.confirm, (dialog, which) -> {
                    // Delete account
                    deleteAccount();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void deleteAccount() {
        binding.progressBar.setVisibility(View.VISIBLE);
        userViewModel.deleteAccount();
        Toast.makeText(this, R.string.account_deleted, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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
