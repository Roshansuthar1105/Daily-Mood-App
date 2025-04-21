package com.example.dailymoodandmentalhealthjournalapplication.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.dailymoodandmentalhealthjournalapplication.R;
import com.example.dailymoodandmentalhealthjournalapplication.data.entity.User;
import com.example.dailymoodandmentalhealthjournalapplication.databinding.ActivityEditProfileBinding;
import com.example.dailymoodandmentalhealthjournalapplication.ui.viewmodels.UserViewModel;
import com.example.dailymoodandmentalhealthjournalapplication.utils.ImageStorageManager;

import java.io.File;
import java.io.IOException;

/**
 * Activity for editing user profile information.
 */
public class EditProfileActivity extends AppCompatActivity {
    private ActivityEditProfileBinding binding;
    private UserViewModel userViewModel;
    private Uri selectedImageUri;
    private ImageStorageManager imageStorageManager;

    private final ActivityResultLauncher<Intent> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    binding.imageViewProfile.setImageURI(selectedImageUri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up view binding
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up toolbar
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.edit_profile);

        // Initialize view model
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Initialize image storage manager
        imageStorageManager = new ImageStorageManager(this);

        // Load user data
        loadUserData();

        // Set up click listeners
        setupClickListeners();
    }

    private void loadUserData() {
        binding.progressBar.setVisibility(View.VISIBLE);
        userViewModel.getCurrentUser().observe(this, user -> {
            binding.progressBar.setVisibility(View.GONE);
            if (user != null) {
                // Set user data
                binding.editTextName.setText(user.getName());

                if (user.getAge() > 0) {
                    binding.editTextAge.setText(String.valueOf(user.getAge()));
                }

                if (user.getGender() != null && !user.getGender().isEmpty()) {
                    String gender = user.getGender();
                    if (gender.equalsIgnoreCase("male")) {
                        binding.radioMale.setChecked(true);
                    } else if (gender.equalsIgnoreCase("female")) {
                        binding.radioFemale.setChecked(true);
                    } else {
                        binding.radioOther.setChecked(true);
                    }
                }

                // Load profile picture if available
                if (user.getProfilePictureUrl() != null && !user.getProfilePictureUrl().isEmpty()) {
                    // Check if it's a local file path
                    File imageFile = new File(user.getProfilePictureUrl());
                    if (imageFile.exists()) {
                        // Load from local file
                        Glide.with(this)
                                .load(imageFile)
                                .placeholder(R.drawable.ic_profile_placeholder)
                                .error(R.drawable.ic_profile_placeholder)
                                .circleCrop()
                                .into(binding.imageViewProfile);
                    } else {
                        // Try loading as a URI string
                        Glide.with(this)
                                .load(user.getProfilePictureUrl())
                                .placeholder(R.drawable.ic_profile_placeholder)
                                .error(R.drawable.ic_profile_placeholder)
                                .circleCrop()
                                .into(binding.imageViewProfile);
                    }
                }
            }
        });
    }

    private void setupClickListeners() {
        // Change profile picture button
        binding.buttonChangeProfilePicture.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(intent);
        });

        // Save button
        binding.buttonSave.setOnClickListener(v -> saveUserProfile());

        // Cancel button
        binding.buttonCancel.setOnClickListener(v -> finish());
    }

    private void saveUserProfile() {
        String name = binding.editTextName.getText().toString().trim();
        String ageStr = binding.editTextAge.getText().toString().trim();

        // Validate name
        if (TextUtils.isEmpty(name)) {
            binding.editTextName.setError("Name is required");
            return;
        }

        // Get age
        int age = 0;
        if (!TextUtils.isEmpty(ageStr)) {
            try {
                age = Integer.parseInt(ageStr);
                if (age < 0 || age > 120) {
                    binding.editTextAge.setError("Please enter a valid age");
                    return;
                }
            } catch (NumberFormatException e) {
                binding.editTextAge.setError("Please enter a valid age");
                return;
            }
        }

        // Get gender
        String gender = "";
        int checkedId = binding.radioGroupGender.getCheckedRadioButtonId();
        if (checkedId == R.id.radioMale) {
            gender = "Male";
        } else if (checkedId == R.id.radioFemale) {
            gender = "Female";
        } else if (checkedId == R.id.radioOther) {
            gender = "Other";
        }

        // Show progress
        binding.progressBar.setVisibility(View.VISIBLE);

        // If image is selected, upload it first
        if (selectedImageUri != null) {
            uploadProfileImage(name, age, gender);
        } else {
            // Otherwise, just update the profile
            updateUserProfile(name, age, gender, null);
        }
    }

    private void uploadProfileImage(String name, int age, String gender) {
        // Save image to local storage
        String imagePath = imageStorageManager.saveImageFromUri(selectedImageUri);

        if (imagePath != null) {
            // Update user profile with local image path
            updateUserProfile(name, age, gender, imagePath);
        } else {
            binding.progressBar.setVisibility(View.GONE);
            Toast.makeText(EditProfileActivity.this, "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUserProfile(String name, int age, String gender, String profilePictureUrl) {
        userViewModel.getCurrentUser().observe(this, user -> {
            if (user != null) {
                // Only update the profile picture URL if a new one was uploaded
                String pictureUrl = profilePictureUrl != null ? profilePictureUrl : user.getProfilePictureUrl();

                // Update user profile
                userViewModel.updateUserProfile(name, age, gender, pictureUrl);

                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
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
