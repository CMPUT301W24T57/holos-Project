package com.example.holosproject;

import android.app.Dialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.Inflater;

/**
 * FileName: AttendeeDashboardEventsAdapter
 * Description: This is the adapter for the RecyclerView that we use to display Events within the Attendee Dashboard. It will display all events the User is currently enrolled in.
 * To be honest, I don't understand how most of it works myself, i copied most of the logic from when I used it for the solo project.
 *  RecyclerView is nice because we can customize it lots, but its a bit of a pain to set up. Luckily I already did that :)

 * Associated with the tem_attendee_dashboard.xml layout.
 **/

public class AttendeeDashboardEventsAdapter extends RecyclerView.Adapter<AttendeeDashboardEventsAdapter.EventViewHolder>  {
    private List<Event> eventList;
    private final String TAG = "Event_details";

    public AttendeeDashboardEventsAdapter(List<Event> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendee_dashboard_event, parent, false);
        return new EventViewHolder(view, this);
    }

    private void showEventDetailsDialog(Context context, Event event) {
        AlertDialog.Builder dispbuilder = new AlertDialog.Builder(context);


        LayoutInflater inflater = LayoutInflater.from(context);
        View diagView = inflater.inflate(R.layout.event_info, null);
        dispbuilder.setView(diagView);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUserId = user.getUid();

        Switch switchPlanToAttend = diagView.findViewById(R.id.plan_to_attend_list);
        switchPlanToAttend.setChecked(event.getAttendees().contains(currentUserId));
        TextView textViewEventName = diagView.findViewById(R.id.textViewEventNameDiag);
        TextView textViewEventDate = diagView.findViewById(R.id.textViewEventDateDiag);
        TextView textViewEventTime = diagView.findViewById(R.id.textViewEventTimeDiag);
        TextView textViewEventLocation = diagView.findViewById(R.id.textViewEventLocationDiag);
        TextView textViewEventAttendeeList = diagView.findViewById(R.id.event_attendee_list);

        textViewEventName.setText("EVENT NAME: " + event.getName());
        textViewEventDate.setText("EVENT DATE: " + event.getDate());
        textViewEventTime.setText("EVENT TIME: " + event.getTime());
        textViewEventLocation.setText("EVENT LOCATION: " + event.getAddress());
        /*String attendeesStr = "Attendees: " + String.join(", ", event.getAttendees());
        textViewEventAttendeeList.setText(attendeesStr);*/
        List<String> attendeeIds1 = event.getAttendees();
        displayAttendeeNames(attendeeIds1, textViewEventAttendeeList, db);

        switchPlanToAttend.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Add the current user to the attendees list if not already included
                if (!event.getAttendees().contains(currentUserId)) {
                    addUserEvent(currentUserId, event.getEventId());
                    event.getAttendees().add(currentUserId);
                }
            } else {
                // Remove the current user from the attendees list
                event.getAttendees().remove(currentUserId);
            }

            // Update the attendees list in Firestore
            db.collection("events").document(event.getEventId())
                    .update("attendees", event.getAttendees())
                    .addOnSuccessListener(aVoid -> {
                        // Update the displayed attendees list
                        List<String> attendeeIds = event.getAttendees();
                        displayAttendeeNames(attendeeIds, textViewEventAttendeeList, db);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error updating attendees list", e);
                    });
        });
        dispbuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface diagdisp, int i) {
                diagdisp.dismiss();

            }
        });

        AlertDialog diag = dispbuilder.create();
        diag.show();
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.textViewEventName.setText(event.getName());
        holder.textViewEventDate.setText(event.getDate());
    }
    /*
    Adds event to the users myEvents
     */
    private void addUserEvent(String userId, String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("userProfiles").document(userId);

        userRef.update("myEvents", FieldValue.arrayUnion(eventId))
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Event added to user's list"))
                .addOnFailureListener(e -> Log.e(TAG, "Error adding event to user's list", e));
    }

    /*
    since names are passed in to event as a uid use the uid to find every string
     */
    private void displayAttendeeNames(List<String> attendeeIds, TextView textViewEventAttendeeList, FirebaseFirestore db) {
        List<String> attendeeNames = new ArrayList<>();

        // Since the counter decrements it ensures that we find all attendees
        AtomicInteger fetchCounter = new AtomicInteger(attendeeIds.size());

        for (String attendeeId : attendeeIds) {
            db.collection("userProfiles").document(attendeeId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Find the name
                            String name = documentSnapshot.getString("name");
                            if (name != null) {
                                attendeeNames.add(name);
                            }
                        }
                        // Decrement the counter and check if all fetches are done
                        if (fetchCounter.decrementAndGet() == 0) {
                            String namesStr = String.join(", ", attendeeNames);
                            textViewEventAttendeeList.setText("Attendees: " + namesStr);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error fetching user profile", e);
                        if (fetchCounter.decrementAndGet() == 0) {
                            String namesStr = String.join(", ", attendeeNames);
                            textViewEventAttendeeList.setText("Attendees: " + namesStr);
                        }
                    });
        }
    }
    @Override
    public int getItemCount() {
        return eventList.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView textViewEventName;
        TextView textViewEventDate;

        EventViewHolder(View itemView, AttendeeDashboardEventsAdapter adapter) {
            super(itemView);
            textViewEventName = itemView.findViewById(R.id.textViewEventName);
            textViewEventDate = itemView.findViewById(R.id.textViewEventDate);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int postn = getAdapterPosition();
                    if (postn != RecyclerView.NO_POSITION) {
                        Event event = adapter.eventList.get(postn);
                        adapter.showEventDetailsDialog(itemView.getContext(), event);
                    }
                }
            });
        }
    }
}
