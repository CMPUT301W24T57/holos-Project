package com.example.holosproject;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class NavigationDrawerUtils {

    public static void updateNavigationHeader(NavigationView navigationView) {
        View headerView = navigationView.getHeaderView(0);
        TextView usernameTextView = headerView.findViewById(R.id.textViewHeaderUsername);
        ImageView profileImageView = headerView.findViewById(R.id.imageViewHeaderProfilePic);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("userProfiles").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            UserProfile userProfile = documentSnapshot.toObject(UserProfile.class);
                            if (userProfile != null) {
                                usernameTextView.setText(userProfile.getName());

                                // Use Glide to load the user's profile picture
                                if (userProfile.getProfileImageUrl() != null && !userProfile.getProfileImageUrl().isEmpty()) {
                                    Glide.with(headerView.getContext())
                                            .load(userProfile.getProfileImageUrl())
                                            .skipMemoryCache(true) // Skip memory cache
                                            .diskCacheStrategy(DiskCacheStrategy.NONE) // Skip disk cache
                                            .placeholder(R.drawable.ic_launcher_foreground) // Replace with your default placeholder
                                            .circleCrop()
                                            .into(profileImageView);
                                }
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle any errors here, such as a log message or a user notification
                    });
        }
    }
}