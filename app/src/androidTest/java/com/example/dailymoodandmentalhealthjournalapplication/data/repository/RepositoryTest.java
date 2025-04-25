package com.example.dailymoodandmentalhealthjournalapplication.data.repository;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.dailymoodandmentalhealthjournalapplication.data.entity.JournalEntry;
import com.example.dailymoodandmentalhealthjournalapplication.data.entity.MoodEntry;
import com.example.dailymoodandmentalhealthjournalapplication.data.entity.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test the repository implementations.
 */
@RunWith(AndroidJUnit4.class)
public class RepositoryTest {
    private UserRepository userRepository;
    private MoodRepository moodRepository;
    private JournalRepository journalRepository;
    private Context context;

    @Before
    public void setup() {
        context = ApplicationProvider.getApplicationContext();
        userRepository = new UserRepository(context.getApplicationContext());
        moodRepository = new MoodRepository(context.getApplicationContext());
        journalRepository = new JournalRepository(context.getApplicationContext());
    }

    @Test
    public void testUserRepository() throws InterruptedException {
        // Create a test user
        String userId = UUID.randomUUID().toString();
        User user = new User(userId, "Test User", "test@example.com");

        // Insert user
        userRepository.insertUser(user);

        // Wait for the operation to complete
        Thread.sleep(1000);

        // Get user by ID
        User retrievedUser = userRepository.getUserByIdSync(userId);
        assertNotNull("User should not be null", retrievedUser);
        assertEquals("User ID should match", userId, retrievedUser.getUserId());
        assertEquals("User name should match", "Test User", retrievedUser.getName());

        // Update user
        retrievedUser.setName("Updated User");
        userRepository.updateUser(retrievedUser);

        // Wait for the operation to complete
        Thread.sleep(1000);

        // Get updated user
        User updatedUser = userRepository.getUserByIdSync(userId);
        assertEquals("User name should be updated", "Updated User", updatedUser.getName());

        // Delete user
        userRepository.deleteUser(updatedUser);
    }

    @Test
    public void testMoodRepository() throws InterruptedException {
        // Create a test user
        String userId = UUID.randomUUID().toString();
        User user = new User(userId, "Test User", "test@example.com");
        userRepository.insertUser(user);

        // Wait for the operation to complete
        Thread.sleep(1000);

        // Create a test mood entry
        MoodEntry moodEntry = new MoodEntry(userId, System.currentTimeMillis(), "HAPPY", 8);
        moodEntry.setNotes("Feeling great today!");

        // Insert mood entry
        final CountDownLatch latch = new CountDownLatch(1);
        final long[] moodEntryId = new long[1];

        moodRepository.insertMoodEntry(moodEntry, id -> {
            moodEntryId[0] = id;
            latch.countDown();
        });

        // Wait for the operation to complete
        latch.await(2, TimeUnit.SECONDS);

        // Verify mood entry was inserted
        assertTrue("Mood entry ID should be positive", moodEntryId[0] > 0);
    }

    @Test
    public void testJournalRepository() throws InterruptedException {
        // Create a test user
        String userId = UUID.randomUUID().toString();
        User user = new User(userId, "Test User", "test@example.com");
        userRepository.insertUser(user);

        // Wait for the operation to complete
        Thread.sleep(1000);

        // Create a test journal entry
        JournalEntry journalEntry = new JournalEntry(userId, System.currentTimeMillis(),
                "Test Journal Entry", "This is a test journal entry content.");
        journalEntry.setTags("test,journal,entry");

        // Insert journal entry
        final CountDownLatch latch = new CountDownLatch(1);
        final long[] journalEntryId = new long[1];

        journalRepository.insertJournalEntry(journalEntry, id -> {
            journalEntryId[0] = id;
            latch.countDown();
        });

        // Wait for the operation to complete
        latch.await(2, TimeUnit.SECONDS);

        // Verify journal entry was inserted
        assertTrue("Journal entry ID should be positive", journalEntryId[0] > 0);
    }
}
