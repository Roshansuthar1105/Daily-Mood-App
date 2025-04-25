package com.example.dailymoodandmentalhealthjournalapplication.data.database;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.dailymoodandmentalhealthjournalapplication.data.dao.JournalEntryDao;
import com.example.dailymoodandmentalhealthjournalapplication.data.dao.MoodEntryDao;
import com.example.dailymoodandmentalhealthjournalapplication.data.dao.UserDao;
import com.example.dailymoodandmentalhealthjournalapplication.data.entity.JournalEntry;
import com.example.dailymoodandmentalhealthjournalapplication.data.entity.MoodEntry;
import com.example.dailymoodandmentalhealthjournalapplication.data.entity.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test the Room database implementation.
 */
@RunWith(AndroidJUnit4.class)
public class AppDatabaseTest {
    private AppDatabase db;
    private UserDao userDao;
    private MoodEntryDao moodEntryDao;
    private JournalEntryDao journalEntryDao;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        userDao = db.userDao();
        moodEntryDao = db.moodEntryDao();
        journalEntryDao = db.journalEntryDao();
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void testUserCrud() throws InterruptedException {
        // Create a test user
        String userId = UUID.randomUUID().toString();
        User user = new User(userId, "Test User", "test@example.com");
        
        // Insert user
        userDao.insert(user);
        
        // Get user by ID
        User retrievedUser = userDao.getUserByIdSync(userId);
        assertNotNull("User should not be null", retrievedUser);
        assertEquals("User ID should match", userId, retrievedUser.getUserId());
        assertEquals("User name should match", "Test User", retrievedUser.getName());
        assertEquals("User email should match", "test@example.com", retrievedUser.getEmail());
        
        // Update user
        retrievedUser.setName("Updated User");
        userDao.update(retrievedUser);
        
        // Get updated user
        User updatedUser = userDao.getUserByIdSync(userId);
        assertEquals("User name should be updated", "Updated User", updatedUser.getName());
        
        // Delete user
        userDao.delete(updatedUser);
        
        // Verify user is deleted
        User deletedUser = userDao.getUserByIdSync(userId);
        assertTrue("User should be deleted", deletedUser == null);
    }

    @Test
    public void testMoodEntryCrud() throws InterruptedException {
        // Create a test user
        String userId = UUID.randomUUID().toString();
        User user = new User(userId, "Test User", "test@example.com");
        userDao.insert(user);
        
        // Create a test mood entry
        MoodEntry moodEntry = new MoodEntry(userId, System.currentTimeMillis(), "HAPPY", 8);
        moodEntry.setNotes("Feeling great today!");
        
        // Insert mood entry
        long moodEntryId = moodEntryDao.insert(moodEntry);
        assertTrue("Mood entry ID should be positive", moodEntryId > 0);
        
        // Get all mood entries for user
        final CountDownLatch latch = new CountDownLatch(1);
        final List[] entries = new List[1];
        
        new Thread(() -> {
            entries[0] = moodEntryDao.getAllMoodEntriesByUser(userId).getValue();
            latch.countDown();
        }).start();
        
        latch.await(2, TimeUnit.SECONDS);
        
        // Verify mood entry was inserted
        if (entries[0] != null) {
            assertEquals("Should have 1 mood entry", 1, entries[0].size());
        }
        
        // Delete mood entry
        moodEntry.setId(moodEntryId);
        moodEntryDao.delete(moodEntry);
    }

    @Test
    public void testJournalEntryCrud() throws InterruptedException {
        // Create a test user
        String userId = UUID.randomUUID().toString();
        User user = new User(userId, "Test User", "test@example.com");
        userDao.insert(user);
        
        // Create a test journal entry
        JournalEntry journalEntry = new JournalEntry(userId, System.currentTimeMillis(), 
                "Test Journal Entry", "This is a test journal entry content.");
        journalEntry.setTags("test,journal,entry");
        
        // Insert journal entry
        long journalEntryId = journalEntryDao.insert(journalEntry);
        assertTrue("Journal entry ID should be positive", journalEntryId > 0);
        
        // Get all journal entries for user
        final CountDownLatch latch = new CountDownLatch(1);
        final List[] entries = new List[1];
        
        new Thread(() -> {
            entries[0] = journalEntryDao.getAllJournalEntriesByUser(userId).getValue();
            latch.countDown();
        }).start();
        
        latch.await(2, TimeUnit.SECONDS);
        
        // Verify journal entry was inserted
        if (entries[0] != null) {
            assertEquals("Should have 1 journal entry", 1, entries[0].size());
        }
        
        // Delete journal entry
        journalEntry.setId(journalEntryId);
        journalEntryDao.delete(journalEntry);
    }
}
