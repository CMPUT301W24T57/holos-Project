package com.example.holosproject;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class OrganizerDashboardEventsAdapter extends RecyclerView.Adapter<OrganizerDashboardEventsAdapter.EventViewHolder> {
    private List<Event> eventList;
    private final String TAG = "o";

    /**
     * Constructor for the OrganizerDashboardEventsAdapter class.
     *
     * @param eventList List of events to be displayed.
     */
    public OrganizerDashboardEventsAdapter(List<Event> eventList) {
        this.eventList = eventList;
    }

    /**
     * Called when RecyclerView needs a new ViewHolder of the given type to represent an item.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.organizer_dashboard_item_event, parent, false);
        return new EventViewHolder(view, this);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return eventList.size();
    }

    /**
     * ViewHolder class for individual event items.
     */
    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView textViewEventName;
        TextView textViewEventDate;
        ImageView imageViewPosterPreview;

        /**
         * Constructor for the EventViewHolder class.
         *
         * @param itemView The item view.
         * @param adapter  The adapter instance.
         */
        EventViewHolder(View itemView, OrganizerDashboardEventsAdapter adapter) {
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
                        adapter.showEventDetailsDial(itemView.getContext(), event);
                    }
                }
            });
        }
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.textViewEventName.setText(event.getName());
        holder.textViewEventDate.setText(String.format("%s, %s", event.getDate(), event.getTime()));

        // Use Glide to load the image
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
     * Displays the details of an event in a dialog.
     *
     * @param context The context.
     * @param event   The event to display.
     */
    private void showEventDetailsDial(Context context, Event event) {
        AlertDialog.Builder dispbuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        View diagView = inflater.inflate(R.layout.organizer_event_info, null);
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

//        TextView textViewEventAttendeeList = diagView.findViewById(R.id.event_attendee_list);
        TextView textViewFull = diagView.findViewById(R.id.textViewFull);

        ArrayList<String> attendees = event.getAttendees();
        Button qrNavButton = diagView.findViewById(R.id.qrNav);
        Button SendNotification = diagView.findViewById(R.id.buttonsendNotification);
        Button AttendeeCheckins = diagView.findViewById(R.id.attendeeCheckins);

        qrNavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, QRGen.class);
                intent.putExtra("contents", event.getEventId());
                v.getContext().startActivity(intent);
            }
        });
        AttendeeCheckins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AttendeeCheckinsActivity.class);
                intent.putExtra("checkins", event.getEventId());
                context.startActivity(intent);
            }
        });

        SendNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSendNotificationDialog(context, event);
            }
        });

        // Click listener for the View Check In Map button
        Button viewCheckInMapButton = diagView.findViewById(R.id.viewCheckInMapButton);
        viewCheckInMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OrganizerMapActivity.class);
                intent.putExtra("EVENT_ID", event.getEventId()); // Pass the event ID to the map activity
                context.startActivity(intent);
            }
        });

        textViewEventName.setText("EVENT NAME: " + event.getName());
        textViewEventDate.setText("EVENT DATE: " + event.getDate());
        textViewEventTime.setText("EVENT TIME: " + event.getTime());
        textViewEventLocation.setText("EVENT LOCATION: " + event.getAddress());

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
                        //displayAttendeeNames(attendeeIds, textViewEventAttendeeList, db);
                    })
                    .addOnFailureListener(e -> {
//                        Log.e(TAG, "Error updating attendees list", e);
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

    /**
     * Adds an event to a user's list of events.
     *
     * @param userId  The ID of the user.
     * @param eventId The ID of the event.
     */
    private void addUserEvent(String userId, String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("userProfiles").document(userId);

        userRef.update("myEvents", FieldValue.arrayUnion(eventId))
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Event added to user's list"))
                .addOnFailureListener(e -> Log.e(TAG, "Error adding event to user's list", e));
    }

    private void showSendNotificationDialog(Context context, Event event) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.send_notification_dialog, null);
        builder.setView(dialogView);

        EditText editTextNotification = dialogView.findViewById(R.id.editTextNotification);
        Button buttonSend = dialogView.findViewById(R.id.sendNotification);
        Button backsendNotification = dialogView.findViewById(R.id.backsendNotification);
        builder.setTitle("");
        AlertDialog dialog = builder.create();
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String notificationText = editTextNotification.getText().toString();
                sendNotificationToAttendees(context, event, notificationText);
                //sendNotificationToAttendees(context, "This is the first notification for this app");
                dialog.dismiss();
            }
        });
        backsendNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void sendNotificationToAttendees(Context context, Event event, String notificationText) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("NewNotis", "New Notis", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        //Intent intent = new Intent(context, ViewAllEventsActivity.class);
        //PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        List<String> attendees = event.getAttendees();

        for (String attendee : attendees) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext(), "NewNotis")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Announcement")
                    .setContentText(notificationText)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            int notificationId = generateUniqueNotificationId();
            notificationManager.notify(notificationId, builder.build());
        }
    }
    private int notificationIdCounter = 0;

    private int generateUniqueNotificationId() {
        return notificationIdCounter++;
    }

}

