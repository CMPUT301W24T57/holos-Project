package com.example.holosproject;

import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


/**
 * FileName: ImageUploader
 * Description: Handles uploading images to firebase
 **/

public class ImageUploader {

    public interface ImageUploadListener {
        void onImageUploadSuccess(String downloadUrl);
        void onImageUploadFailure(Exception e);
    }

    private final FirebaseStorage storage;
    private final FirebaseFirestore firestore;
    private final FirebaseAuth auth;
    private ImageUploadListener listener;

    public ImageUploader(ImageUploadListener listener) {
        this.storage = FirebaseStorage.getInstance();
        this.firestore = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
        this.listener = listener;
    }

    public void uploadProfileImage(Uri imageUri) {
        if (auth.getCurrentUser() == null) {
            listener.onImageUploadFailure(new Exception("Not Authenticated"));
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        StorageReference profileImageRef = storage.getReference("profileImages/" + userId + ".jpg");

        profileImageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    saveImageUrlToUserProfile(downloadUrl);
                }))
                .addOnFailureListener(e -> listener.onImageUploadFailure(e));
    }

    private void saveImageUrlToUserProfile(String imageUrl) {
        String userId = auth.getCurrentUser().getUid();

        firestore.collection("userProfiles").document(userId)
                .update("profileImageUrl", imageUrl)
                .addOnSuccessListener(aVoid -> listener.onImageUploadSuccess(imageUrl))
                .addOnFailureListener(e -> listener.onImageUploadFailure(e));
    }

    // Set a new listener or null to remove
    public void setImageUploadListener(ImageUploadListener listener) {
        this.listener = listener;
    }
}
