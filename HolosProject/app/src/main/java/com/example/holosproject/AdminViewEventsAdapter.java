package com.example.holosproject;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

/**
 * FileName: AdminViewEventsAdapter
 * Description: Adapter for the RecyclerView used within the Admin View Events page. It also contains logic for deleting events from Firebase.
 * AdminViewEventsAdapter is associated with admin_view_events.xml and admin_view_events_item_event.xml
 **/
public class AdminViewEventsAdapter extends RecyclerView.Adapter<AdminViewEventsAdapter.ViewHolder> {

    private List<Event> events;
    private LayoutInflater inflater;

    /**
     * Constructor for AdminViewEventsAdapter.
     *
     * @param context The context.
     * @param events  The list of events.
     */
    public AdminViewEventsAdapter(Context context, List<Event> events) {
        this.inflater = LayoutInflater.from(context);
        this.events = events;
    }

    /**
     * Called when the RecyclerView needs a new ViewHolder to represent an item.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.admin_view_events_item_event, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewEventName, textViewEventDate, textViewEventTime, textViewEventAddress, textViewEventCreator, textViewEventId;
        ImageView eventPosterImageView;

        ViewHolder(View itemView) {
            super(itemView);
            textViewEventName = itemView.findViewById(R.id.textViewEventName);
            textViewEventDate = itemView.findViewById(R.id.textViewEventDate);
            textViewEventTime = itemView.findViewById(R.id.textViewEventTime);
            textViewEventAddress = itemView.findViewById(R.id.textViewEventAddress);
            textViewEventCreator = itemView.findViewById(R.id.textViewEventCreator);
            textViewEventId = itemView.findViewById(R.id.textViewEventId);
            eventPosterImageView = itemView.findViewById(R.id.eventPosterImageViewAdmin);
        }
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the item at the given position.
     * @param position The position of the item within the adapter's data set.
     */

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Event event = events.get(position);
        holder.textViewEventName.setText(event.getName());
        holder.textViewEventDate.setText(event.getDate());
        holder.textViewEventTime.setText(event.getTime());
        holder.textViewEventAddress.setText(event.getAddress());
        holder.textViewEventCreator.setText(event.getCreator());
        holder.textViewEventId.setText(event.getEventId());

        // Load the event poster image
        if (event.getImageUrl() != null && !event.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(event.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_background) // Use a placeholder
                    .into(holder.eventPosterImageView);
        }

        holder.itemView.setOnClickListener(v -> showDeleteConfirmation(event));
    }

    /**
     * Shows a delete confirmation dialog.
     *
     * @param event The event to be deleted.
     */
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

    /**
     * Deletes an event from Firebase. If inside test mode, deletes the mock provided events.
     *
     * @param eventId The ID of the event to be deleted.
     */
    private void deleteEventFromFirebase(String eventId) {
        // If we are in test mode: Find and delete the mock event from the local list.
        if (AdminViewEventsActivity.isTestMode) {
            // Locate the event to delete by its ID in test mode
            int position = -1;
            for (int i = 0; i < events.size(); i++) {
                if (events.get(i).getEventId().equals(eventId)) {
                    position = i;
                    break;
                }
            }
            if (position != -1) {
                events.remove(position);
                notifyItemRemoved(position);
                Toast.makeText(inflater.getContext(), "Event deleted (test mode)", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(inflater.getContext(), "Event not found (test mode)", Toast.LENGTH_SHORT).show();
            }
            return; // Exit the method to avoid attempting to delete from Firebase in test mode
        }

        else {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            // Retrieve the event to get the imageUrl
            db.collection("events").document(eventId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        String imageUrl = documentSnapshot.getString("imageUrl");
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            // Delete the image from Firebase Storage
                            StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
                            imageRef.delete().addOnSuccessListener(aVoid -> {
                                // Image successfully deleted
                                // Now delete the event document from Firestore
                                deleteFirestoreEvent(eventId);
                            }).addOnFailureListener(e -> {
                                Log.e(TAG, "Error deleting event image: ", e);
                                Toast.makeText(inflater.getContext(), "Error deleting event image", Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            // If there's no image, just delete the event document
                            deleteFirestoreEvent(eventId);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error fetching event for deletion: ", e);
                        Toast.makeText(inflater.getContext(), "Error fetching event for deletion", Toast.LENGTH_SHORT).show();
                    });
        }
    }


    /**
     * Deletes an event from Firestore database.
     *
     * @param eventId The ID of the event to be deleted.
     */
    private void deleteFirestoreEvent(String eventId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("events").document(eventId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    int position = findPositionByEventId(eventId);
                    if (position != -1) {
                        events.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, events.size());
                        Toast.makeText(inflater.getContext(), "Event deleted successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting event: ", e);
                    Toast.makeText(inflater.getContext(), "Error deleting event", Toast.LENGTH_SHORT).show();
                });
    }



    /**
     * Finds the position of an event in the list by its ID.
     *
     * @param eventId The ID of the event to find.
     * @return The position of the event in the list, or -1 if not found.
     */
    private int findPositionByEventId(String eventId) {
        for (int i = 0; i < events.size(); i++) {
            if (events.get(i).getEventId().equals(eventId)) {
                return i;
            }
        }
        return -1; // Event not found
    }

    /**
     * Returns the total number of events in the data set.
     *
     * @return The total number of events.
     */

    @Override
    public int getItemCount() {
        return events.size();
    }


}
