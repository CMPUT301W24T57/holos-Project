package com.example.holosproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

/**
 * A dummy version of the Event Display screen (so we don't have to deal with Firebase).
 */
public class TestEventDisplay extends AppCompatActivity {

    /**
     * Handle a test event.
     * Retrieves mock events from the data provider and matches the event ID to display event details.
     * @param eventID The ID of the event to be handled.
     */

    private void handleTestEvent(String eventID) {
        List<Event> mockEvents = MockDataProvider.getMockEvents();
        for (Event event : mockEvents) {
            if (Objects.equals(event.getEventId(), eventID)) {
                String name = event.getName();
                String date = event.getDate();
                String time = event.getTime();
                String posterUrl = event.getImageUrl();

                TextView dateDisplay = findViewById(R.id.event_Date);
                TextView creatorDisplay = findViewById(R.id.event_Creator);
                TextView eventDisplay = findViewById(R.id.eventTitle);
                ImageView posterDisplay = findViewById(R.id.eventPoster);
                ImageView avatarDisplay = findViewById(R.id.event_creatorAvatar);
                Picasso.get().load(posterUrl).into(posterDisplay);

                dateDisplay.setText(getString(R.string.prefixDate, date, time));
                eventDisplay.setText(name);
            }
        }
    }

    /**
     * Navigates back to the dashboard activity.
     *
     * @param eventID The ID of the event.
     */
    private void backToDashboard(String eventID) {
            Intent intent = new Intent(this, TestAttendeeDashboardActivity.class);
            intent.putExtra("title", eventID);
            startActivity(intent);
    }

    /**
     * Called when the activity is starting. Sets up the layout and handles event details display.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_display);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String eventID = bundle.getString("contents");
            handleTestEvent(eventID);
            Button rsvpButton = findViewById(R.id.rsvpButton);
            rsvpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    backToDashboard(eventID);
                }
            });
        }
    }
}