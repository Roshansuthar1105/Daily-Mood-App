package com.example.dailymoodandmentalhealthjournalapplication.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.dailymoodandmentalhealthjournalapplication.R;
import com.example.dailymoodandmentalhealthjournalapplication.auth.LocalAuthManager;
import com.example.dailymoodandmentalhealthjournalapplication.databinding.FragmentProfileBinding;
import com.example.dailymoodandmentalhealthjournalapplication.ui.auth.LoginActivity;
import com.example.dailymoodandmentalhealthjournalapplication.ui.profile.EditProfileActivity;
import com.example.dailymoodandmentalhealthjournalapplication.ui.settings.SettingsActivity;
import com.example.dailymoodandmentalhealthjournalapplication.ui.viewmodels.UserViewModel;

/**
 * Fragment for displaying and managing user profile.
 */
public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private UserViewModel userViewModel;
    private LocalAuthManager authManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize view model and auth manager
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        authManager = LocalAuthManager.getInstance(requireContext());

        // Load user profile
        loadUserProfile();

        // Set up buttons
        setupButtons();
    }

    private void loadUserProfile() {
        binding.progressBar.setVisibility(View.VISIBLE);
        userViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
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
            Intent intent = new Intent(requireContext(), EditProfileActivity.class);
            startActivity(intent);
        });
        
        // Settings button
        binding.buttonSettings.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), SettingsActivity.class);
            startActivity(intent);
        });
        
        // Logout button
        binding.buttonLogout.setOnClickListener(v -> {
            authManager.signOut();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        
        // Delete account button
        binding.buttonDeleteAccount.setOnClickListener(v -> {
            showDeleteAccountConfirmationDialog();
        });
    }

    private void showDeleteAccountConfirmationDialog() {
        new AlertDialog.Builder(requireContext())
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
        Toast.makeText(requireContext(), R.string.account_deleted, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
