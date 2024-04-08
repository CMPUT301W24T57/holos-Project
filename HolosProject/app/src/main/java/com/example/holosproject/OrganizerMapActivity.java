package com.example.holosproject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.List;


/**
 * OrganizerMapActivity displays a map for organizers.
 * NOTE: 2024-04-03 4:46pm
 * When testing marker functionality on the computer, markers were not visible.
 * It may be due to a bug or emulator issues.
 * It works on the phone! - Nicolas, Apr. 6
 */


public class OrganizerMapActivity extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    private static final String TAG = OrganizerMapActivity.class.getSimpleName();

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_map); // Set the layout for the activity

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Coordinates for central Edmonton as default view
        LatLng edmonton = new LatLng(53.5444, -113.4909);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(edmonton, 13)); // Adjust this zoom level as you see fit (10 shows you all of edmonton)
        // Get the event ID from the intent
        String eventId = getIntent().getStringExtra("EVENT_ID"); // Make sure "EVENT_ID" matches the key used when starting this activity

        if (eventId != null) {
            fetchAndPlotCheckIns(eventId);
        } else {
            Log.d(TAG, "No event ID provided.");
        }
    }

    /**
     * This method is called when the back button is clicked.
     * It finishes the activity and navigates back.
     *
     * @param view The view that was clicked.
     */
    public void onBackClicked(View view) {
        // This will simply finish the activity and go back
        finish();
    }

    /**
     * Fetches and plots check-ins on the map for a given event.
     *
     * @param eventId The ID of the event to fetch and plot check-ins for.
     */
    private void fetchAndPlotCheckIns(String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference eventRef = db.collection("events").document(eventId);

        eventRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<GeoPoint> locations = (List<GeoPoint>) documentSnapshot.get("locations");
                if (locations != null) {
                    for (GeoPoint geoPoint : locations) {
                        LatLng latLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(latLng));
                    }
                } else {
                    Log.d(TAG, "No locations to display.");
                }
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Error fetching event details", e));
    }
}