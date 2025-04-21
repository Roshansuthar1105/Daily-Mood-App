package com.example.dailymoodandmentalhealthjournalapplication.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Entity class representing a mood entry in the application.
 */
@Entity(tableName = "mood_entries",
        foreignKeys = @ForeignKey(
                entity = User.class,
                parentColumns = "userId",
                childColumns = "userId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("userId"), @Index("date")}
)
public class MoodEntry {
    @PrimaryKey(autoGenerate = true)
    private long id;
    
    @NonNull
    private String userId;
    
    private long date;
    private String moodType; // HAPPY, SAD, ANGRY, ANXIOUS, NEUTRAL
    private int moodIntensity; // 1-10 scale
    private String notes;
    private long createdAt;
    private long updatedAt;

    public MoodEntry(@NonNull String userId, long date, String moodType, int moodIntensity) {
        this.userId = userId;
        this.date = date;
        this.moodType = moodType;
        this.moodIntensity = moodIntensity;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getMoodType() {
        return moodType;
    }

    public void setMoodType(String moodType) {
        this.moodType = moodType;
    }

    public int getMoodIntensity() {
        return moodIntensity;
    }

    public void setMoodIntensity(int moodIntensity) {
        this.moodIntensity = moodIntensity;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }
}
