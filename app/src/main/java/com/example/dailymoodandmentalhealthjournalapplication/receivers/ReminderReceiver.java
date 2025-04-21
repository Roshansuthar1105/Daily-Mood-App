package com.example.dailymoodandmentalhealthjournalapplication.receivers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.dailymoodandmentalhealthjournalapplication.R;
import com.example.dailymoodandmentalhealthjournalapplication.ui.mood.MoodSelectionActivity;

/**
 * Broadcast receiver for handling daily reminders.
 */
public class ReminderReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "mood_reminder_channel";
    private static final int NOTIFICATION_ID = 1001;

    @Override
    public void onReceive(Context context, Intent intent) {
        // Create notification channel for Android O and above
        createNotificationChannel(context);

        // Create intent for when the notification is tapped
        Intent notificationIntent = new Intent(context, MoodSelectionActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(context.getString(R.string.reminder_title))
                .setContentText(context.getString(R.string.reminder_text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        // Show the notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());

        // Reschedule the reminder for tomorrow
        rescheduleReminder(context);
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void rescheduleReminder(Context context) {
        // Get the current reminder settings
        com.example.dailymoodandmentalhealthjournalapplication.utils.NotificationManager notificationManager =
                new com.example.dailymoodandmentalhealthjournalapplication.utils.NotificationManager(context);

        // Check if reminders are enabled using SharedPreferences directly
        android.content.SharedPreferences prefs = context.getSharedPreferences("notification_prefs", Context.MODE_PRIVATE);
        boolean isEnabled = prefs.getBoolean("reminder_enabled", false);

        if (isEnabled) {
            // Get reminder time from SharedPreferences
            int hour = prefs.getInt("reminder_hour", 20); // Default to 8:00 PM
            int minute = prefs.getInt("reminder_minute", 0);

            // Reschedule the reminder for tomorrow at the same time
            notificationManager.scheduleReminder(hour, minute);
        }
    }
}
