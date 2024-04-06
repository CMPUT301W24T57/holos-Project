package com.example.holosproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * FileName: AttendeeDashboardEventsAdapter
 * Description: This is the adapter for the RecyclerView that we use to display Events within the Attendee Dashboard. It will display all events the User is currently enrolled in.
 * To be honest, I don't understand how most of it works myself, i copied most of the logic from when I used it for the solo project.
 *  RecyclerView is nice because we can customize it lots, but its a bit of a pain to set up. Luckily I already did that :)

 * Associated with the tem_attendee_dashboard.xml layout.
 **/

public class AttendeeDashboardEventsAdapter extends RecyclerView.Adapter<AttendeeDashboardEventsAdapter.EventViewHolder> {
    private List<Event> eventList;
    private final String TAG = "Event_details";

    /**
     * Constructs an AttendeeDashboardEventsAdapter with the given list of events.
     * @param eventList The list of events to display.
     */
    public AttendeeDashboardEventsAdapter(List<Event> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendee_dashboard_event, parent, false);
        return new EventViewHolder(view, this);
    }

    /**
     * Displays the details of the event in a dialog.
     * @param context The context of the application.
     * @param event The event to display details of.
     */
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
        TextView textViewEventLocation = diagView.findViewById(R.id.textViewEventLocationDiag);
        ImageView eventPoster = diagView.findViewById(R.id.event_poster);
        TextView textViewFull = diagView.findViewById(R.id.textViewFull);

        textViewEventName.setText(event.getName());
        textViewEventDate.setText("Date: " + event.getDate() + " at " + event.getTime());
        textViewEventLocation.setText("Location: " + event.getAddress());
        Picasso.get().load(event.getImageUrl()).into(eventPoster);


        List<String> attendeeIds1 = event.getAttendees();

//        displayAttendeeNames(attendeeIds1, textViewEventAttendeeList, db);
        int numAttendees = attendeeIds1.size();
        int eventLimit = event.getLimit();
        Log.d("NumA","Num attendees: "+numAttendees);
        if (numAttendees >= eventLimit) {
            Log.d("HideFull","We should Hide full");
            if (!attendeeIds1.contains(currentUserId)) {
            switchPlanToAttend.setVisibility(View.GONE);}
            textViewEventName.setText(event.getName()+" (Full)");
        }

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
                removeUserEvent(currentUserId, event.getEventId());
            }

            // Update the attendees list in Firestore
            db.collection("events").document(event.getEventId())
                    .update("attendees", event.getAttendees())
                    .addOnSuccessListener(aVoid -> {
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
        if (event.getAttendees().size() >= event.getLimit()){
            holder.textViewEventName.setText(event.getName() + " (Full)");
        }
        else{holder.textViewEventName.setText(event.getName());}

        holder.textViewEventDate.setText(event.getDate());
        if (event.getImageUrl() != null && !event.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(event.getImageUrl())
                    .into(holder.imageViewPosterPreview);
        } else {
            // Here you can set a default image or a placeholder
            holder.imageViewPosterPreview.setImageResource(R.drawable.ic_launcher_background); // using launcher background as a placeholder for now
        }
    }

    /**
     * Adds an event to the user's myEvents.
     * @param userId The ID of the user.
     * @param eventId The ID of the event to add.
     */
    private void addUserEvent(String userId, String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("userProfiles").document(userId);

        userRef.update("myEvents", FieldValue.arrayUnion(eventId))
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Event added to user's list"))
                .addOnFailureListener(e -> Log.e(TAG, "Error adding event to user's list", e));
    }

    /**
     * Removes an event from the user's myEvents list.
     * @param userId The ID of the user.
     * @param eventId The ID of the event to remove.
     */
    private void removeUserEvent(String userId, String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("userProfiles").document(userId);

        userRef.update("myEvents", FieldValue.arrayRemove(eventId))
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Event removed"))
                .addOnFailureListener(e -> Log.e(TAG, "Error removing event", e));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView textViewEventName;
        TextView textViewEventDate;

        ImageView imageViewPosterPreview;

        EventViewHolder(View itemView, AttendeeDashboardEventsAdapter adapter) {
            super(itemView);
            textViewEventName = itemView.findViewById(R.id.textViewEventName);
            textViewEventDate = itemView.findViewById(R.id.textViewEventDate);
            imageViewPosterPreview = itemView.findViewById(R.id.imageViewPosterPreview);

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