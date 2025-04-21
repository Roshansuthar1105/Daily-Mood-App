package com.example.dailymoodandmentalhealthjournalapplication.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class for managing image storage locally.
 */
public class ImageStorageManager {
    private static final String TAG = "ImageStorageManager";
    private static final String PROFILE_IMAGES_DIR = "profile_images";
    
    private final Context context;
    
    public ImageStorageManager(Context context) {
        this.context = context;
    }
    
    /**
     * Save an image from a Uri to local storage.
     *
     * @param imageUri The Uri of the image to save
     * @return The path to the saved image, or null if saving failed
     */
    public String saveImageFromUri(Uri imageUri) {
        try {
            // Create directory if it doesn't exist
            File directory = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), PROFILE_IMAGES_DIR);
            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    Log.e(TAG, "Failed to create directory");
                    return null;
                }
            }
            
            // Create a unique filename
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "PROFILE_" + timeStamp + ".jpg";
            File outputFile = new File(directory, fileName);
            
            // Convert Uri to Bitmap
            InputStream inputStream = context.getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            if (inputStream != null) {
                inputStream.close();
            }
            
            // Compress and save the bitmap
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
            outputStream.flush();
            outputStream.close();
            
            // Return the absolute path
            return outputFile.getAbsolutePath();
        } catch (IOException e) {
            Log.e(TAG, "Error saving image", e);
            return null;
        }
    }
    
    /**
     * Delete an image from local storage.
     *
     * @param imagePath The path to the image to delete
     * @return True if the image was deleted successfully, false otherwise
     */
    public boolean deleteImage(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return false;
        }
        
        File file = new File(imagePath);
        return file.exists() && file.delete();
    }
    
    /**
     * Get a Uri for an image path.
     *
     * @param imagePath The path to the image
     * @return The Uri for the image, or null if the path is invalid
     */
    public Uri getUriForImage(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }
        
        File file = new File(imagePath);
        if (!file.exists()) {
            return null;
        }
        
        return Uri.fromFile(file);
    }
}
