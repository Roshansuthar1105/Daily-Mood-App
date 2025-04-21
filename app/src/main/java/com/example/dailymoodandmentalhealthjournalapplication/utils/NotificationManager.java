package com.example.dailymoodandmentalhealthjournalapplication.utils;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.dailymoodandmentalhealthjournalapplication.R;
import com.example.dailymoodandmentalhealthjournalapplication.ui.MainActivity;

import java.util.concurrent.TimeUnit;

/**
 * Utility class for managing notifications and reminders.
 */
public class NotificationManager {
    private static final String CHANNEL_ID = "mood_journal_channel";
    private static final String CHANNEL_NAME = "Daily Reminders";
    private static final String CHANNEL_DESCRIPTION = "Daily reminders to log your mood and journal entries";
    private static final String WORK_TAG = "daily_reminder_work";
    private static final int NOTIFICATION_ID = 1001;
    private static final String PREF_NAME = "notification_prefs";
    private static final String PREF_REMINDER_ENABLED = "reminder_enabled";
    private static final String PREF_REMINDER_HOUR = "reminder_hour";
    private static final String PREF_REMINDER_MINUTE = "reminder_minute";

    private final Context context;

    public NotificationManager(Context context) {
        this.context = context;
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            android.app.NotificationManager notificationManager = context.getSystemService(android.app.NotificationManager.class);

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    android.app.NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(CHANNEL_DESCRIPTION);

            notificationManager.createNotificationChannel(channel);
        }
    }

    public void scheduleReminder(int hour, int minute) {
        // Save reminder settings to preferences
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(PREF_REMINDER_ENABLED, true)
                .putInt(PREF_REMINDER_HOUR, hour)
                .putInt(PREF_REMINDER_MINUTE, minute)
                .apply();

        Data inputData = new Data.Builder()
                .putInt("hour", hour)
                .putInt("minute", minute)
                .build();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build();

        PeriodicWorkRequest reminderWorkRequest = new PeriodicWorkRequest.Builder(
                ReminderWorker.class,
                24, TimeUnit.HOURS)
                .setConstraints(constraints)
                .setInputData(inputData)
                .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_TAG,
                ExistingPeriodicWorkPolicy.REPLACE,
                reminderWorkRequest
        );
    }

    public void cancelReminders() {
        // Save reminder settings to preferences
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(PREF_REMINDER_ENABLED, false)
                .apply();

        WorkManager.getInstance(context).cancelUniqueWork(WORK_TAG);
    }

    /**
     * Check if reminders are enabled.
     *
     * @return True if reminders are enabled, false otherwise
     */
    public boolean isReminderEnabled() {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getBoolean(PREF_REMINDER_ENABLED, false);
    }

    /**
     * Get the hour of the day for the reminder.
     *
     * @return The hour of the day (0-23)
     */
    public int getReminderHour() {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getInt(PREF_REMINDER_HOUR, 20); // Default to 8:00 PM
    }

    /**
     * Get the minute of the hour for the reminder.
     *
     * @return The minute of the hour (0-59)
     */
    public int getReminderMinute() {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getInt(PREF_REMINDER_MINUTE, 0);
    }

    public void showReminderNotification(String title, String message) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        try {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        } catch (SecurityException e) {
            // Handle missing notification permission
            e.printStackTrace();
        }
    }

    /**
     * Worker class for handling daily reminders.
     */
    public static class ReminderWorker extends Worker {
        public ReminderWorker(Context context, WorkerParameters params) {
            super(context, params);
        }

        @Override
        public Result doWork() {
            NotificationManager notificationManager = new NotificationManager(getApplicationContext());
            notificationManager.showReminderNotification(
                    "Daily Mood Check-in",
                    "Don't forget to log your mood and journal entry for today!"
            );
            return Result.success();
        }
    }
}
