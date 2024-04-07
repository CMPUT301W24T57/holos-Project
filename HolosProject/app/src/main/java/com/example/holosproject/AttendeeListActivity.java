package com.example.holosproject;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * FileName: AdminViewProfilesActivity
 * Description: Admin can view all user profiles, and tap on them to prompt a deletion

 * AdminViewProfilesActivity is associated with the admin_view_profiles.xml, and the admin_view_profiles_list_item.xml
 **/

public class AttendeeListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminViewProfilesAdapter adapter;
    private List<UserProfile> profiles = new ArrayList<>();; // Declare profiles as a member variable

    // Static variable to control test mode
    public static boolean isTestMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_view_profiles);
        String eventID = getIntent().getStringExtra("EVENT_ID");
        TextView diffTitle = findViewById(R.id.textView);
        diffTitle.setText("View All Attendees");
        recyclerView = findViewById(R.id.recyclerViewProfiles);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminViewProfilesAdapter(AttendeeListActivity.this, profiles);
        recyclerView.setAdapter(adapter);

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            finish();
        });
        fetchAttendees(eventID);
    }

    /**
     * Fetches all attendees of a given event
     * @param eventId: the event to fetch attendees for
     */
    private void fetchAttendees(String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        ArrayList<String> attendeeIds1 = (ArrayList<String>) documentSnapshot.get("attendees");
                        if (attendeeIds1 != null) {
                            for (String ID : attendeeIds1) {
                                db.collection("userProfiles").document(ID).get().addOnSuccessListener(documentSnapshot1 -> {
                                    UserProfile profile = documentSnapshot1.toObject(UserProfile.class);
                                    profiles.add(profile);
                                    adapter.notifyDataSetChanged();
                                });
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("AttendeeCheckinsActivity", "Could not get check-ins", e);
                });
    }
}