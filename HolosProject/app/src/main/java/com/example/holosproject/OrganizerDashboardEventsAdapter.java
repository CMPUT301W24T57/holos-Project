package com.example.holosproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class OrganizerDashboardEventsAdapter extends RecyclerView.Adapter<OrganizerDashboardEventsAdapter.EventViewHolder>  {
    private List<Event> eventList;

    public OrganizerDashboardEventsAdapter(List<Event> eventList) {

        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.organizer_dashboard, parent, false);
        return new EventViewHolder(view, this);
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


        EventViewHolder(View itemView, OrganizerDashboardEventsAdapter adapter) {
            super(itemView);
            textViewEventName = itemView.findViewById(R.id.textViewEventName);
            textViewEventDate = itemView.findViewById(R.id.textViewEventDate);

        }
    }
}
