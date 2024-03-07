package com.example.holosproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class OrganizerDashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private OrganizerDashboardEventsAdapter eventAdapter;
    private List<Event> eventList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_dashboard);

        recyclerView = findViewById(R.id.recycler_view_events);
        FloatingActionButton fabAddEvent = findViewById(R.id.fab_add_event);

        eventList = new ArrayList<>();
        eventAdapter = new OrganizerDashboardEventsAdapter(eventList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(eventAdapter);

        // Dummy data for testing
        eventList.add(new Event("Event 1", "November 7 2024"));
        eventList.add(new Event("Event 2", "November 8 2024"));
        eventAdapter.notifyDataSetChanged();

        fabAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewEvent();
            }
        });
    }
    public void createNewEvent(){
        Intent intent = new Intent(this, AddEventActivity.class);
        startActivity(intent);
    }
}
