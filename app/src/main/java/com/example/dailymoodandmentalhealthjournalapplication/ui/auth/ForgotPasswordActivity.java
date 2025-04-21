package com.example.dailymoodandmentalhealthjournalapplication.ui.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dailymoodandmentalhealthjournalapplication.R;
import com.example.dailymoodandmentalhealthjournalapplication.databinding.ActivityForgotPasswordBinding;

/**
 * Activity for password reset.
 */
public class ForgotPasswordActivity extends AppCompatActivity {
    private ActivityForgotPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up view binding
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set up toolbar
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.forgot_password);

        // Set up click listeners
        setupClickListeners();
    }

    private void setupClickListeners() {
        // Reset password button
        binding.buttonResetPassword.setOnClickListener(v -> resetPassword());

        // Back button
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void resetPassword() {
        String email = binding.editTextEmail.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(email)) {
            binding.editTextEmail.setError("Email is required");
            return;
        }

        // Show progress
        binding.progressBar.setVisibility(View.VISIBLE);

        // In the local version, we can't actually reset passwords
        // Just show a message and return to login screen
        binding.progressBar.setVisibility(View.GONE);
        Toast.makeText(this,
                "Password reset is not available in the offline version. Please create a new account.",
                Toast.LENGTH_LONG).show();
        finish();
    }
}