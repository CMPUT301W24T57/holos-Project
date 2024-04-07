package com.example.holosproject;

import android.content.Context;
import android.content.SharedPreferences;


// Referenced from OpenAI. (2024). ChatGPT (Apr 7 version) [Large language model]. https://chat.openai.com/chat
public class UploadManager {
    private static final String PREF_NAME = "ImageUploadPref";
    private static final String KEY_IMAGE_UPLOADED = "image_uploaded";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public UploadManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setImageUploaded(boolean uploaded) {
        editor.putBoolean(KEY_IMAGE_UPLOADED, uploaded);
        editor.apply();
    }

    public boolean isImageUploaded() {
        return sharedPreferences.getBoolean(KEY_IMAGE_UPLOADED, false);
    }
}