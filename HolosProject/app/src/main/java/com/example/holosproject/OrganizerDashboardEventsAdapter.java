package com.example.holosproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
/**
        * FileName: OrganizerDashboardEventsAdapter
        * Description: This is the adapter for the RecyclerView that we use to display Events within the Organizer Dashboard. It will display all events the User is currently enrolled in.
        * Associated with the organizer_dashboard_item_event.xml layout.
        **/
public class OrganizerDashboardEventsAdapter extends RecyclerView.Adapter<OrganizerDashboardEventsAdapter.EventViewHolder> {
    private List<Event> eventList;

    // Constructor to initialize the adapter with a list of events
    public OrganizerDashboardEventsAdapter(List<Event> eventList) {
        this.eventList = eventList;
    }

    // Creates a new ViewHolder when needed
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout for the event item view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.organizer_dashboard_item_event, parent, false);
        return new EventViewHolder(view);
    }

    // Binds data to the ViewHolder
    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        // Retrieve the event at the given position
        Event event = eventList.get(position);
        // Set the event name and date/time to the respective TextViews
        holder.eventNameTextView.setText(event.getName());
        holder.eventDateTextView.setText(String.format("%s, %s", event.getDate(), event.getTime()));
    }

    // Returns the total number of events in the list
    @Override
    public int getItemCount() {
        return eventList.size();
    }

    // ViewHolder class for holding event item views
    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventNameTextView; // TextView to display the event name
        TextView eventDateTextView; // TextView to display the event date/time

        // Constructor to initialize the ViewHolder with the event item view
        EventViewHolder(View itemView) {
            super(itemView);
            // Find and assign the TextViews from the layout
            eventNameTextView = itemView.findViewById(R.id.textViewEventName);
            eventDateTextView = itemView.findViewById(R.id.textViewEventDate);
        }
    }
}
