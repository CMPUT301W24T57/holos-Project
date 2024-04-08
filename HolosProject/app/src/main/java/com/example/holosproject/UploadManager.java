package com.example.holosproject;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * Manages the upload status of images.
 */
public class UploadManager {
    private static final String PREF_NAME = "ImageUploadPref";
    private static final String KEY_IMAGE_UPLOADED = "image_uploaded";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    /**
     * Constructs a new UploadManager instance.
     *
     * @param context The context to access shared preferences.
     */
    public UploadManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * Sets the upload status of the image.
     *
     * @param uploaded True if the image is uploaded, false otherwise.
     */
    public void setImageUploaded(boolean uploaded) {
        editor.putBoolean(KEY_IMAGE_UPLOADED, uploaded);
        editor.apply();
    }

    /**
     * Retrieves the upload status of the image.
     *
     * @return True if the image is uploaded, false otherwise.
     */
    public boolean isImageUploaded() {
        return sharedPreferences.getBoolean(KEY_IMAGE_UPLOADED, false);
    }
}
