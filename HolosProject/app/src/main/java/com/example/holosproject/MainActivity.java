package com.example.holosproject;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    Button scanButton;
    Button genNavButton;

    // Declaring a private variable to hold a reference to the Firestore database
    private FirebaseFirestore database;
    // This next line is an example of what it would look like to declare a CollectionReference Variable,
    // In Firestore, a collection is a group of documents. A CollectionReference is a reference to a specific collection in the Firestore database.
    private CollectionReference userAccountNamesRef;
    private CollectionReference testRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scanButton = findViewById(R.id.scan_btn);
        genNavButton = findViewById(R.id.gen_btn);
        scanButton.setOnClickListener(v-> {
            scanQRCode();
        });
        genNavButton.setOnClickListener(v-> {
            gotoQRGen();
        });

        // Initializing the Firestore database instance when the activity is created
        database = FirebaseFirestore.getInstance();
        // This next line initializes "userAccountNamesRef" by obtaining a reference to the "Profile Account Names" collection in the Firestore database.
        // userAccountNamesRef holds a reference to the "Profile Account Names" collection in our database.
        //userAccountNamesRef = database.collection("Profile Account Names");
        testRef = database.collection("NWQRTest");
    }

    private void scanQRCode() { // basic QR code scan
        ScanOptions options = new ScanOptions();
        options.setBeepEnabled(false);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    private void gotoQRGen() {
        Intent intent = new Intent(this, QRGen.class);
        startActivity(intent);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result->{ //basic popup after scanning to test things
       if(result.getContents() != null) {

           String qrText = result.getContents();

           DocumentReference docRef = database.collection("NWQRTest").document(qrText);
           docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
               @Override
               public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                   if (task.isSuccessful()) {
                       DocumentSnapshot document = task.getResult();
                       if (document.exists()) {
                           AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                           builder.setTitle("Result");
                           builder.setMessage(qrText);
                           builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { // dismisses the popup
                               @Override
                               public void onClick(DialogInterface dialog, int which) {
                                   dialog.dismiss();
                               }
                           }).show();
                       } else {
                           AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                           builder.setTitle("Result");
                           builder.setMessage("Not Found 1");
                           builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { // dismisses the popup
                               @Override
                               public void onClick(DialogInterface dialog, int which) {
                                   dialog.dismiss();
                               }
                           }).show();
                       }
                   } else {
                       Log.d("Firestore", "Database Error");
                   }
               }
           });
       }
    });
}