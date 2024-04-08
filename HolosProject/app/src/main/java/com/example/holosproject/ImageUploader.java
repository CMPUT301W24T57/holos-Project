package com.example.holosproject;

import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


/**
 * FileName: ImageUploader
 * Description: Handles uploading images to firebase
 * Note: As of 2024-03-26 Only works for profile images. Will work on implementing with other types of photos as well.
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

    /**
     * Constructor to initialize the ImageUploader.
     * @param listener The listener to handle image upload events.
     */
    public ImageUploader(ImageUploadListener listener) {
        this.storage = FirebaseStorage.getInstance();
        this.firestore = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
        this.listener = listener;
    }

    /**
     * Uploads the profile image to Firebase Storage and updates the profile image URL in Firestore.
     * @param imageUri The URI of the image to upload.
     */
    public void uploadProfileImage(Uri imageUri) {
        if (auth.getCurrentUser() == null) {
            // User is not authenticated
            listener.onImageUploadFailure(new Exception("Not Authenticated"));
            return;
        }

        String userId = auth.getCurrentUser().getUid();
        StorageReference profileImageRef = storage.getReference("profileImages/" + userId + ".jpg");

        // Upload the image to Firebase Storage
        profileImageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> profileImageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    // Get the download URL of the uploaded image
                    String downloadUrl = uri.toString();
                    saveImageUrlToUserProfile(downloadUrl); // Save the image URL to user profile
                }))
                .addOnFailureListener(e -> listener.onImageUploadFailure(e)); // Notify listener on failure
    }

    /**
     * Saves the profile image URL to the user's profile in Firestore.
     * @param imageUrl The URL of the uploaded profile image.
     */
    private void saveImageUrlToUserProfile(String imageUrl) {
        String userId = auth.getCurrentUser().getUid();

        // Update the profile image URL in Firestore
        firestore.collection("userProfiles").document(userId)
                .update("profileImageUrl", imageUrl)
                .addOnSuccessListener(aVoid -> listener.onImageUploadSuccess(imageUrl)) // Notify listener on success
                .addOnFailureListener(e -> listener.onImageUploadFailure(e)); // Notify listener on failure
    }

    /**
     * Sets a new listener to handle image upload events.
     * @param listener The listener to set, or null to remove the listener.
     */
    public void setImageUploadListener(ImageUploadListener listener) {
        this.listener = listener;
    }

    /**
     * Interface to handle image upload events.
     */
    public interface ImageUploadListener {
        /**
         * Called when image upload is successful.
         * @param downloadUrl The download URL of the uploaded image.
         */
        void onImageUploadSuccess(String downloadUrl);

        /**
         * Called when image upload fails.
         * @param e The exception indicating the cause of the failure.
         */
        void onImageUploadFailure(Exception e);
    }
}