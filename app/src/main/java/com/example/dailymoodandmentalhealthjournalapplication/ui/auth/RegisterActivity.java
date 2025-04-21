package com.example.dailymoodandmentalhealthjournalapplication.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dailymoodandmentalhealthjournalapplication.R;
import com.example.dailymoodandmentalhealthjournalapplication.auth.LocalAuthManager;
import com.example.dailymoodandmentalhealthjournalapplication.databinding.ActivityRegisterBinding;
import com.example.dailymoodandmentalhealthjournalapplication.ui.MainActivity;

/**
 * Activity for user registration.
 */
public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private LocalAuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up view binding
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize auth manager
        authManager = LocalAuthManager.getInstance(this);

        // Set up click listeners
        setupClickListeners();
    }

    private void setupClickListeners() {
        // Register button
        binding.buttonRegister.setOnClickListener(v -> registerUser());

        // Login button
        binding.buttonLogin.setOnClickListener(v -> {
            finish(); // Go back to login activity
        });
    }

    private void registerUser() {
        String name = binding.editTextName.getText().toString().trim();
        String email = binding.editTextEmail.getText().toString().trim();
        String password = binding.editTextPassword.getText().toString().trim();
        String confirmPassword = binding.editTextConfirmPassword.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(name)) {
            binding.editTextName.setError("Name is required");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            binding.editTextEmail.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            binding.editTextPassword.setError("Password is required");
            return;
        }

        if (password.length() < 6) {
            binding.editTextPassword.setError("Password must be at least 6 characters");
            return;
        }

        if (!password.equals(confirmPassword)) {
            binding.editTextConfirmPassword.setError("Passwords do not match");
            return;
        }

        // Show progress
        binding.progressBar.setVisibility(View.VISIBLE);

        // Attempt registration
        authManager.registerUser(email, password, name, new LocalAuthManager.AuthCallback() {
            @Override
            public void onSuccess(String userId) {
                binding.progressBar.setVisibility(View.GONE);

                // Registration successful
                Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();

                // Navigate to main activity
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String errorMessage) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(RegisterActivity.this, "Registration failed: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
