package com.example.holosproject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;





public class AttendeeCheckinsActivity extends AppCompatActivity {
    private ListView AttendeeCheckins;
    private AttendeeCheckinsAdapter adapter;
    private List<AttendeeCheckin> attendeeCheckins;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendee_checkin_act);
        String eventId = getIntent().getStringExtra("checkins");
        AttendeeCheckins = findViewById(R.id.AttendeeCheckins);
        attendeeCheckins = new ArrayList<>();
        // Tessssstttt
        //attendeeCheckins.add(new AttendeeCheckin("Michael", 2));
        //attendeeCheckins.add(new AttendeeCheckin("Flock", 4));

        adapter = new AttendeeCheckinsAdapter(this, attendeeCheckins);
        AttendeeCheckins.setAdapter(adapter);


        Button backButton = findViewById(R.id.BackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        fetchAttendeeCheckins(eventId);
    }


    // How I fetch the events, checkins and count from firebase. Still not tested though
    private void fetchAttendeeCheckins(String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").document(eventId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> checkIns = (List<String>) documentSnapshot.get("checkIns");
                        if (checkIns != null) {
                            Map<String, AttendeeCheckin> checkinMap = new HashMap<>();
                            for (String attendeeId : checkIns) {
                                AttendeeCheckin attendeeCheckin = checkinMap.getOrDefault(attendeeId, new AttendeeCheckin("", 0));
                                attendeeCheckin.setCheckinCount(attendeeCheckin.getCheckinCount() + 1);
                                checkinMap.put(attendeeId, attendeeCheckin);
                                fetchAttendeeName(attendeeId, attendeeCheckin);
                            }
                            attendeeCheckins.clear();
                            attendeeCheckins.addAll(checkinMap.values());
                            adapter.notifyDataSetChanged();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("AttendeeCheckinsActivity", "Could not get check-ins", e);
                });
    }
    
    private void fetchAttendeeName(String attendeeId, AttendeeCheckin attendeeCheckin) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("userProfiles").document(attendeeId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String name = documentSnapshot.getString("name");
                        if (name != null) {
                            attendeeCheckin.setName(name);
                            adapter.notifyDataSetChanged();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("AttendeeCheckinsActivity", "Could not get user names", e);
                });
    }



}
