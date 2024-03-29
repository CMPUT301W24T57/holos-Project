package com.example.holosproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

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
        attendeeCheckins.add(new AttendeeCheckin("Michael", 2));
        attendeeCheckins.add(new AttendeeCheckin("Flock", 4));

        adapter = new AttendeeCheckinsAdapter(this, attendeeCheckins);
        AttendeeCheckins.setAdapter(adapter);


        Button backButton = findViewById(R.id.BackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
