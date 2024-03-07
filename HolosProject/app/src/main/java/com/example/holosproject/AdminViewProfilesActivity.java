package com.example.holosproject;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminViewProfilesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminViewProfilesAdapter adapter;
    private List<UserProfile> profiles; // Declare profiles as a member variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_view_profiles);

        recyclerView = findViewById(R.id.recyclerViewProfiles);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the adapter and set it to the RecyclerView
        recyclerView.setAdapter(adapter);

        // Fetch user profiles from Firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("userProfiles")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<UserProfile> profiles = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            UserProfile profile = document.toObject(UserProfile.class);
                            profile.setUid(document.getId()); // Save the document ID for deletion
                            profiles.add(profile);
                        }
                        adapter = new AdminViewProfilesAdapter(AdminViewProfilesActivity.this, profiles);
                        adapter.setClickListener(this::onItemClick);
                        recyclerView.setAdapter(adapter);
                    } else {
                        // Handle the error
                    }
                });

    }

    private void onItemClick(View view, int position) {
        UserProfile selectedProfile = profiles.get(position);
        new AlertDialog.Builder(this)
                .setTitle("Delete Profile")
                .setMessage("Are you sure you want to delete this profile?")
                .setPositiveButton("Yes", (dialog, which) -> deleteProfile(selectedProfile))
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteProfile(UserProfile profile) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("userProfiles").document(profile.getUid())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Remove the profile from the list and notify the adapter
                    profiles.remove(profile);
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Handle the error
                });
    }

}
