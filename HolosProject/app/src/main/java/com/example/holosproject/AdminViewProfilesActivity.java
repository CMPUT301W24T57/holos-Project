package com.example.holosproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * FileName: AdminViewProfilesActivity
 * Description: Admin can view all user profiles, and tap on them to prompt a deletion

 * AdminViewProfilesActivity is associated with the admin_view_profiles.xml, and the admin_view_profiles_list_item.xml
 **/

public class AdminViewProfilesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminViewProfilesAdapter adapter;
    private List<UserProfile> profiles; // Declare profiles as a member variable

    // Static variable to control test mode
    public static boolean isTestMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_view_profiles);

        recyclerView = findViewById(R.id.recyclerViewProfiles);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        if (isTestMode) {
            // If we are in test mode, set the recyclerview to contain our mock data.
            profiles = MockDataProvider.getMockProfiles();

            adapter = new AdminViewProfilesAdapter(AdminViewProfilesActivity.this, profiles);
            adapter.setClickListener(this::onItemClick);
            recyclerView.setAdapter(adapter);

        } else {
            // Fetch user profiles from Firebase
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("userProfiles")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            profiles = new ArrayList<>(); // Use the class member variable
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
    }

    /**
     * Handles click events on user profiles in the RecyclerView.
     *
     * @param view     The clicked view.
     * @param position The position of the clicked item in the RecyclerView.
     */
    private void onItemClick(View view, int position) {
        UserProfile selectedProfile = profiles.get(position);
        new AlertDialog.Builder(this)
                .setTitle("Delete Profile")
                .setMessage("Are you sure you want to delete this profile?")
                .setPositiveButton("Yes", (dialog, which) -> deleteProfile(selectedProfile))
                .setNegativeButton("No", null)
                .show();
    }

    /**
     * Deletes a user profile from Firebase.
     *
     * @param profile The profile to be deleted.
     */
    private void deleteProfile(UserProfile profile) {
        if (isTestMode) {
            // If we are in test mode, remove the profile from the local list and update the adapter in test mode.
            profiles.remove(profile);
            adapter.notifyDataSetChanged();
            showToast("Profile Deleted (Test Mode)");

            // If we are not in test mode, load the profiles from firebase.
        } else {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("userProfiles").document(profile.getUid())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        // Remove the profile from the list and notify the adapter
                        profiles.remove(profile);
                        adapter.notifyDataSetChanged();
                        showToast("Profile Deleted");
                    })
                    .addOnFailureListener(e -> {
                        showToast("Error: Did not delete profile");
                    });
        }
    }

    /**
     * Shows a toast message.
     *
     * @param message The message to be displayed in the toast.
     */
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    /**
     * Enables test mode, used when running app tests.
     */
    // Static method to enable test mode
    public static void enableTestMode() {
        isTestMode = true;
    }
    /**
     * Disables test mode, used when running app tests.
     */
    public static void disableTestMode() {
        isTestMode = false;
    }

}