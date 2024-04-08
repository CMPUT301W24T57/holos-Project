package com.example.holosproject;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.print.PrintHelper;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

/**
 * This activity is used to display a bare-bones event poster with a promo code
 */

public class PrintDisplay extends AppCompatActivity {

    private final FirebaseFirestore database = FirebaseFirestore.getInstance();
    private final CollectionReference eventsRef = database.collection("events");

    private Button printButton;

    private ImageView promoDisplay;


    /**
     * Retrieves and displays the details of the event specified by the eventID.
     *
     * @param eventID The ID of the event to be displayed.
     */
    private void handleCheckIn(String eventID) {
        DocumentReference docRef = eventsRef.document(eventID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String name = document.getString("name");
                        String date = document.getString("date");
                        String time = document.getString("time");
                        String address = document.getString("address");
                        String creator = document.getString("creator");
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        String eventId = document.getId();
                        String posterUrl = document.getString("imageUrl");
                        String customUrl = document.getString("qrUrl");
                        QRGEncoder qrgEncoder2;

                        TextView dateDisplay = findViewById(R.id.event_Date);
                        TextView creatorDisplay = findViewById(R.id.event_Creator);
                        TextView eventDisplay = findViewById(R.id.eventTitle);
                        TextView eventLocation = findViewById(R.id.event_Location);
                        ImageView posterDisplay = findViewById(R.id.eventPoster);
                        ImageView avatarDisplay = findViewById(R.id.event_creatorAvatar);
                        Picasso.get().load(posterUrl).into(posterDisplay);

                        if (customUrl == null) {
                            qrgEncoder2 = new QRGEncoder(eventID, null, QRGContents.Type.TEXT, 250);
                            Bitmap bitmap2 = qrgEncoder2.getBitmap(0);
                            promoDisplay.setImageBitmap(bitmap2);
                        }
                        else {
                            Picasso.get().load(customUrl).into(promoDisplay);
                        }

                        dateDisplay.setText(getString(R.string.prefixDate, date, time));
                        eventLocation.setText("Location: " + address);
                        eventDisplay.setText(name);
                        db.collection("userProfiles").document(creator).get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        String creatorName = documentSnapshot.getString("name");
                                        String creatorAvatar = documentSnapshot.getString("profileImageUrl");
                                        if (name != null) {
                                            creatorDisplay.setText(getString(R.string.prefixOrganizer, creatorName));
                                        }
                                        if (creatorAvatar != null) {
                                            Picasso.get().load(creatorAvatar).into(avatarDisplay);
                                        }
                                    }
                                });
                    }
                }
            }
        });
    }

    private void handlePromo(String eventID) {
        DocumentReference docRef = eventsRef.document(eventID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String name = document.getString("name");
                        String date = document.getString("date");
                        String time = document.getString("time");
                        String address = document.getString("address");
                        String creator = document.getString("creator");
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        String eventId = document.getId();
                        String posterUrl = document.getString("imageUrl");
                        String customUrl = document.getString("customQRContents");
                        QRGEncoder qrgEncoder2;

                        TextView dateDisplay = findViewById(R.id.event_Date);
                        TextView creatorDisplay = findViewById(R.id.event_Creator);
                        TextView eventDisplay = findViewById(R.id.eventTitle);
                        TextView eventLocation = findViewById(R.id.event_Location);
                        ImageView posterDisplay = findViewById(R.id.eventPoster);
                        ImageView avatarDisplay = findViewById(R.id.event_creatorAvatar);
                        Picasso.get().load(posterUrl).into(posterDisplay);

                        qrgEncoder2 = new QRGEncoder("promo" + eventID, null, QRGContents.Type.TEXT, 250);
                        Bitmap bitmap2 = qrgEncoder2.getBitmap(0);
                        promoDisplay.setImageBitmap(bitmap2);

                        dateDisplay.setText(getString(R.string.prefixDate, date, time));
                        eventLocation.setText("Location: " + address);
                        eventDisplay.setText(name);
                        db.collection("userProfiles").document(creator).get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        String creatorName = documentSnapshot.getString("name");
                                        String creatorAvatar = documentSnapshot.getString("profileImageUrl");
                                        if (name != null) {
                                            creatorDisplay.setText(getString(R.string.prefixOrganizer, creatorName));
                                        }
                                        if (creatorAvatar != null) {
                                            Picasso.get().load(creatorAvatar).into(avatarDisplay);
                                        }
                                    }
                                });
                    }
                }
            }
        });
    }

    /**
     * Initializes the activity, sets up the layout, and handles window insets.
     * Also handles the print button click listener.
     * @param savedInstanceState The saved instance state.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.print_display);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        printButton = findViewById(R.id.printButton);
        printButton.setVisibility(View.VISIBLE);
        promoDisplay=findViewById(R.id.eventCode);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String eventID = bundle.getString("contents");
            if (eventID.contains("promo")) {
                eventID = eventID.replace("promo", "");
                handlePromo(eventID);
            }
            else {
                handleCheckIn(eventID);
            }
            printButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    print();
                }
            });
        }
    }

    // Printing functions referenced from https://stackoverflow.com/questions/58037591/how-to-print-a-layout-with-print-document-adapter-in-android

    /**
     * Handles printing the current layout (turns it into a printable .pdf).
     */
    private void print() {
        printButton.setVisibility(View.GONE);

        View v = findViewById(android.R.id.content);
        Bitmap bmp = Bitmap.createBitmap(v.getWidth(), v.getHeight() - 225, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        v.draw(c);
        PrintHelper photoPrinter = new PrintHelper(PrintDisplay.this);
        photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FILL);
        photoPrinter.printBitmap("layout.png", bmp);
    }
}