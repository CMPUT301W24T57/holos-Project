package com.example.holosproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AttendeeCheckinsAdapter extends ArrayAdapter<AttendeeCheckin> {

    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = database.collection("userProfiles");

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
        DocumentReference docRef = usersRef.document(attendeeCheckin.getName());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        AttendeeName.setText((String) document.get("name"));
                    }
                }
            }
        });
        AttendeeName.setText(attendeeCheckin.getName());
        CheckinCount.setText(String.valueOf(attendeeCheckin.getCheckinCount()));

        return convertView;
    }

}
