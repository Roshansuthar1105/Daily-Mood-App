package com.example.dailymoodandmentalhealthjournalapplication.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dailymoodandmentalhealthjournalapplication.R;
import com.example.dailymoodandmentalhealthjournalapplication.auth.LocalAuthManager;
import com.example.dailymoodandmentalhealthjournalapplication.databinding.ActivityLoginBinding;
import com.example.dailymoodandmentalhealthjournalapplication.ui.MainActivity;

/**
 * Activity for user login.
 */
public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private LocalAuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up view binding
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize auth manager
        authManager = LocalAuthManager.getInstance(this);

        // Check if user is already logged in
        if (authManager.isUserLoggedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        // Set up click listeners
        setupClickListeners();
    }

    private void setupClickListeners() {
        // Login button
        binding.buttonLogin.setOnClickListener(v -> loginUser());

        // Register button
        binding.buttonRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        // Forgot password button
        binding.textViewForgotPassword.setOnClickListener(v -> {
            Toast.makeText(this, "This feature is not available in the offline version", Toast.LENGTH_SHORT).show();
        });

        // Hide Google sign-in button in offline version
        binding.buttonGoogleSignIn.setVisibility(View.GONE);
        binding.textViewOr.setVisibility(View.GONE);
    }

    private void loginUser() {
        String email = binding.editTextEmail.getText().toString().trim();
        String password = binding.editTextPassword.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(email)) {
            binding.editTextEmail.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            binding.editTextPassword.setError("Password is required");
            return;
        }

        // Show progress
        binding.progressBar.setVisibility(View.VISIBLE);

        // Attempt login
        authManager.loginUser(email, password, new LocalAuthManager.AuthCallback() {
            @Override
            public void onSuccess(String userId) {
                binding.progressBar.setVisibility(View.GONE);

                // Navigate to main activity
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onFailure(String errorMessage) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, "Login failed: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }


}
