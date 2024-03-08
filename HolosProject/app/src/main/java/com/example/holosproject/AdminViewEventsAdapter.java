package com.example.holosproject;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * FileName: AdminViewEventsAdapter
 * Description: Adapter for the RecylcerView used within the Admin View Events page, also has logic for deleting events from firebase

 * AdminViewEventsAdapter is associated with admin_view_events.xml and admin_view_events_item_event.xml
 **/

public class AdminViewEventsAdapter extends RecyclerView.Adapter<AdminViewEventsAdapter.ViewHolder> {

    private List<Event> events;
    private LayoutInflater inflater;

    public AdminViewEventsAdapter(Context context, List<Event> events) {
        this.inflater = LayoutInflater.from(context);
        this.events = events;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.admin_view_events_item_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Event event = events.get(position);
        holder.textViewEventName.setText(event.getName());
        holder.textViewEventDate.setText(event.getDate());
        holder.textViewEventTime.setText(event.getTime());
        holder.textViewEventAddress.setText(event.getAddress());
        holder.textViewEventCreator.setText(event.getCreator());
        holder.textViewEventId.setText(event.getEventId());

        holder.itemView.setOnClickListener(v -> showDeleteConfirmation(event));
    }

    private void showDeleteConfirmation(Event event) {
        new AlertDialog.Builder(inflater.getContext())
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete this event?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    deleteEventFromFirebase(event.getEventId());
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void deleteEventFromFirebase(String eventId) {
        if (eventId == null || eventId.trim().isEmpty()) {
            Toast.makeText(inflater.getContext(), "Invalid event ID", Toast.LENGTH_SHORT).show();
            return; // Stop if the eventId is not valid
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").document(eventId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    int position = findPositionByEventId(eventId);
                    if (position != -1) {
                        events.remove(position);
                        notifyItemRemoved(position);
                        Toast.makeText(inflater.getContext(), "Event deleted successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting event: ", e);
                    Toast.makeText(inflater.getContext(), "Error deleting event", Toast.LENGTH_SHORT).show();
                });
    }

    private int findPositionByEventId(String eventId) {
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getEventId().equals(eventId)) {
                return i;
            }
        }
        return -1; // Event not found
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewEventName, textViewEventDate, textViewEventTime, textViewEventAddress, textViewEventCreator, textViewEventId;

        ViewHolder(View itemView) {
            super(itemView);
            textViewEventName = itemView.findViewById(R.id.textViewEventName);
            textViewEventDate = itemView.findViewById(R.id.textViewEventDate);
            textViewEventTime = itemView.findViewById(R.id.textViewEventTime);
            textViewEventAddress = itemView.findViewById(R.id.textViewEventAddress);
            textViewEventCreator = itemView.findViewById(R.id.textViewEventCreator);
            textViewEventId = itemView.findViewById(R.id.textViewEventId);
        }
    }
}
