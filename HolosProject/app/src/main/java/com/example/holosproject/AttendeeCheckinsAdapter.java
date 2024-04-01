package com.example.holosproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class AttendeeCheckinsAdapter extends ArrayAdapter<AttendeeCheckin> {
    public AttendeeCheckinsAdapter(Context context, List<AttendeeCheckin> attendeeCheckins) {
        super(context, 0, attendeeCheckins);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AttendeeCheckin attendeeCheckin = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.attendee_checkin, parent, false);
        }

        TextView AttendeeName = convertView.findViewById(R.id.AttendeeName);
        TextView CheckinCount = convertView.findViewById(R.id.CheckinCount);
        AttendeeName.setText(attendeeCheckin.getName());
        CheckinCount.setText(String.valueOf(attendeeCheckin.getCheckinCount()));

        return convertView;
    }

}
