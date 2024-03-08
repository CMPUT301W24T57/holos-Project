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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_organizer_dashboard_event, parent, false);
        return new EventViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.textViewEventName.setText(event.getName());
        holder.textViewEventDate.setText(event.getDate());
        holder.textViewEventTime.setText(event.getTime());

    }

    @Override
    public int getItemCount() {

        return eventList.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView textViewEventName;
        TextView textViewEventDate;
        TextView textViewEventTime;


        EventViewHolder(View itemView, OrganizerDashboardEventsAdapter adapter) {
            super(itemView);
            textViewEventName = itemView.findViewById(R.id.text_event_name);
            textViewEventDate = itemView.findViewById(R.id.text_event_date);
            textViewEventTime = itemView.findViewById(R.id.text_event_time);

        }
    }
}
