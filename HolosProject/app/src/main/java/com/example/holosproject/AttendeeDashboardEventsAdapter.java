package com.example.holosproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * FileName: AttendeeDashboardEventsAdapter
 * Description: This is the adapter for the RecyclerView that we use to display Events within the Attendee Dashboard. It will display all events the User is currently enrolled in.
 * To be honest, I don't understand how most of it works myself, i copied most of the logic from when I used it for the solo project.
 *  RecyclerView is nice because we can customize it lots, but its a bit of a pain to set up. Luckily I already did that :)

 * Associated with the tem_attendee_dashboard.xml layout.
 **/

public class AttendeeDashboardEventsAdapter extends RecyclerView.Adapter<AttendeeDashboardEventsAdapter.EventViewHolder>  {
    private List<Event> eventList;

    public AttendeeDashboardEventsAdapter(List<Event> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendee_dashboard_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.textViewEventName.setText(event.getName());
        holder.textViewEventDate.setText(event.getDate());
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView textViewEventName;
        TextView textViewEventDate;

        EventViewHolder(View itemView) {
            super(itemView);
            textViewEventName = itemView.findViewById(R.id.textViewEventName);
            textViewEventDate = itemView.findViewById(R.id.textViewEventDate);
        }
    }
}
