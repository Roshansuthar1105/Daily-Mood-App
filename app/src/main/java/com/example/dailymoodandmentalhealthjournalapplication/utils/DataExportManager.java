package com.example.dailymoodandmentalhealthjournalapplication.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import androidx.core.content.FileProvider;

import com.example.dailymoodandmentalhealthjournalapplication.data.entity.JournalEntry;
import com.example.dailymoodandmentalhealthjournalapplication.data.entity.MoodEntry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Utility class for exporting data to CSV or PDF.
 */
public class DataExportManager {
    private final Context context;
    private static final String CSV_HEADER_MOOD = "Date,Mood Type,Mood Intensity,Notes";
    private static final String CSV_HEADER_JOURNAL = "Date,Title,Content,Tags,Favorite";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public DataExportManager(Context context) {
        this.context = context;
    }

    /**
     * Export mood entries to a CSV file.
     *
     * @param moodEntries The list of mood entries to export
     * @return The URI of the exported file
     */
    public Uri exportMoodEntriesToCSV(List<MoodEntry> moodEntries) throws IOException {
        StringBuilder csvData = new StringBuilder();
        csvData.append(CSV_HEADER_MOOD).append("\n");

        for (MoodEntry entry : moodEntries) {
            String date = DATE_FORMAT.format(new Date(entry.getDate()));
            String moodType = entry.getMoodType();
            int intensity = entry.getMoodIntensity();
            String notes = entry.getNotes() != null ? entry.getNotes().replace(",", ";") : "";

            csvData.append(date).append(",")
                  .append(moodType).append(",")
                  .append(intensity).append(",")
                  .append(notes).append("\n");
        }

        return writeToFile(csvData.toString(), "mood_entries.csv");
    }

    /**
     * Export journal entries to a CSV file.
     *
     * @param journalEntries The list of journal entries to export
     * @return The URI of the exported file
     */
    public Uri exportJournalEntriesToCSV(List<JournalEntry> journalEntries) throws IOException {
        StringBuilder csvData = new StringBuilder();
        csvData.append(CSV_HEADER_JOURNAL).append("\n");

        for (JournalEntry entry : journalEntries) {
            String date = DATE_FORMAT.format(new Date(entry.getDate()));
            String title = entry.getTitle().replace(",", ";");
            String content = entry.getContent().replace(",", ";").replace("\n", " ");
            String tags = entry.getTags() != null ? entry.getTags().replace(",", ";") : "";
            boolean favorite = entry.isFavorite();

            csvData.append(date).append(",")
                  .append(title).append(",")
                  .append(content).append(",")
                  .append(tags).append(",")
                  .append(favorite).append("\n");
        }

        return writeToFile(csvData.toString(), "journal_entries.csv");
    }

    /**
     * Write data to a file in the app's external files directory.
     *
     * @param data     The data to write
     * @param fileName The name of the file
     * @return The URI of the file
     */
    private Uri writeToFile(String data, String fileName) throws IOException {
        File dir = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "exports");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(data.getBytes());
        fos.close();

        return FileProvider.getUriForFile(
                context,
                context.getPackageName() + ".fileprovider",
                file
        );
    }

    /**
     * Share the exported file.
     *
     * @param fileUri  The URI of the file to share
     * @param mimeType The MIME type of the file
     * @return The intent for sharing the file
     */
    public Intent createShareIntent(Uri fileUri, String mimeType) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType(mimeType);
        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return shareIntent;
    }
}
