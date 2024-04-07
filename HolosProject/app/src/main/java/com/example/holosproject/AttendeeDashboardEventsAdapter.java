package com.example.holosproject;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Transaction;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
    private static final String ONESIGNAL_APP_ID = "44fb7829-68a6-45d8-b153-61c241864b10";

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
        // Made the updating of the switch use firestore transactions.
        // This way even if users open the dialog at the same time the limit will never be surpassed.
        switchPlanToAttend.setOnCheckedChangeListener((buttonView, isChecked) -> {
            final DocumentReference eventRef = db.collection("events").document(event.getEventId());
            db.runTransaction((Transaction.Function<Void>) transaction -> {
                        DocumentSnapshot eventSnapshot = transaction.get(eventRef);
                        List<String> currentAttendees = (List<String>) eventSnapshot.get("attendees");
                        if (currentAttendees == null) {
                            currentAttendees = new ArrayList<>();
                        }
                        if (isChecked) {
                            // Trying to add the current user
                            if (!currentAttendees.contains(currentUserId) && currentAttendees.size() < eventLimit) {
                                currentAttendees.add(currentUserId);
                                transaction.update(eventRef, "attendees", currentAttendees);
                                addUserEvent(currentUserId, event.getEventId());
                                int num = (event.getAttendees().size() + 1);
                                // These are the milestones that we will send alerts at
                                if (num == 1 || num == 5 || num == 10 || num == 50 || num == 100 || num == 500 || num == 1000) {
                                    String message = "We have reached milestone: " + num + " attendees";
                                    sendNotificationThroughServer(message,"Milestone", event.getCreator());
                                }
                                if ( num == eventLimit){
                                    sendNotificationThroughServer("We have reached maximum capacity: " + num, "Milestone", event.getCreator());
                                }
                            } else {
                                new Handler(Looper.getMainLooper()).post(() ->
                                        Toast.makeText(context, "Error: The Event is Full", Toast.LENGTH_SHORT).show());
                                return null;
                            }
                        } else {
                            // Removing current User
                            if (currentAttendees.contains(currentUserId)) {
                                currentAttendees.remove(currentUserId);
                                transaction.update(eventRef, "attendees", currentAttendees);
                                removeUserEvent(currentUserId, event.getEventId());
                            }
                        }
                        return null; // Transaction must return null if void
                    }).addOnSuccessListener(aVoid -> Log.d(TAG, "Transaction successfully completed"))
                    .addOnFailureListener(e -> Log.e(TAG, "Transaction failed: ", e));
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

    /**
     * Sends a notification to a specific user using oneSignal api
     * @param messageContent
     * The content of the message.
     * @param notificationTitle
     * The notification header.
     * @param targetExternalUserId
     * The user we will send the notification to.
     */
    public void sendNotificationThroughServer(String messageContent, String notificationTitle, String targetExternalUserId) {
        // Your OneSignal App REST API key
        // Not safe at all btw 💀💀💀💀
        String REST_API_KEY = "NzhhNmY2MDMtODJiYy00MDUyLWFmNTEtZjM5Y2MzYzQxYTNl";


        String jsonBody = "{\"app_id\": \"" + ONESIGNAL_APP_ID + "\", \"contents\": {\"en\": \"" + messageContent + "\"}, \"include_external_user_ids\": [\"" + targetExternalUserId + "\"], \"headings\": {\"en\": \"" + notificationTitle + "\"}}";
        // Create OkHttpClient instance
        OkHttpClient client = new OkHttpClient();

        // Create the request body
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonBody);

        // Build the request
        Request request = new Request.Builder()
                .url("https://onesignal.com/api/v1/notifications")
                .addHeader("Authorization", "Basic " + REST_API_KEY)
                .post(body)
                .build();

        // Execute the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("Failed to send the message: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    System.out.println("Failed to send the message: " + response.body().string());
                } else {
                    System.out.println("Message sent successfully!");
                }
            }
        });
    }

}