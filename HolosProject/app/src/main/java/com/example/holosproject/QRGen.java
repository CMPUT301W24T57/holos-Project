package com.example.holosproject;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.OutputStream;
import java.util.Objects;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;

public class QRGen extends AppCompatActivity {

    private String customUrl;

    private String customContents = null;

    private QRGEncoder qrgEncoder;

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_qrgen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView checkInView = findViewById(R.id.checkInDisplay);
        ImageView promoView = findViewById(R.id.promoQRDisplay);

        Bundle bundle = getIntent().getExtras();
        String eventID;
        if (bundle != null) {
            eventID = bundle.getString("contents");
        } else {
            eventID = null;
        }
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference eventRef = database.collection("events").document(eventID);
        eventRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        customUrl = (String) document.get("qrUrl");
                        if (customUrl != null) {
                            Picasso.get().load(customUrl).into(checkInView);
                            customContents = (String) document.get("customQRContents");
                        }
                        else {
                            QRGEncoder qrgEncoder2 = new QRGEncoder("promo" + eventID, null, QRGContents.Type.TEXT, 250);
                            Bitmap bitmap2 = qrgEncoder2.getBitmap(0);
                            checkInView.setImageBitmap(bitmap2);
                        }
                    }
                } else {
                    Log.d("Firestore", "Database Error");
                }
            }
        });

        QRGEncoder qrgEncoder1 = new QRGEncoder("promo" + eventID, null, QRGContents.Type.TEXT, 250);
        Bitmap bitmap1 = qrgEncoder1.getBitmap(0);
        promoView.setImageBitmap(bitmap1);

        // SAVING QR CODE FUNCTIONALITY
        findViewById(R.id.save_btn).setOnClickListener(v -> {
            OutputStream fos;
            try {
                if (customContents != null) {
                    QRGEncoder qrgEncoder = new QRGEncoder(customContents, null, QRGContents.Type.TEXT, 200);
                }
                else {
                    QRGEncoder qrgEncoder = new QRGEncoder(eventID, null, QRGContents.Type.TEXT, 200);
                }
                Bitmap bitmap = qrgEncoder.getBitmap(0);
                ContentResolver resolver = getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, eventID + "Code" + ".jpg");
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                Objects.requireNonNull(fos);
                Toast.makeText(QRGen.this, "Image saved to" + imageUri.getPath(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        findViewById(R.id.share_btn).setOnClickListener(v -> {
            OutputStream fos;
            try {
                if (customContents != null) {
                    QRGEncoder qrgEncoder = new QRGEncoder(customContents, null, QRGContents.Type.TEXT, 200);
                }
                else {
                    QRGEncoder qrgEncoder = new QRGEncoder(eventID, null, QRGContents.Type.TEXT, 200);
                }
                Bitmap bitmap = qrgEncoder.getBitmap(0);
                ContentResolver resolver = getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, eventID + "Code" + ".jpg");
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                Objects.requireNonNull(fos);
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setType("image/png");
                startActivity(intent);
                // Delete the image after sharing (maybe unneeded)
                // resolver.delete(imageUri, null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        findViewById(R.id.save_btn2).setOnClickListener(v -> {
            OutputStream fos;
            try {
                qrgEncoder = new QRGEncoder("promo" + eventID, null, QRGContents.Type.TEXT, 200);
                Bitmap bitmap = qrgEncoder.getBitmap(0);
                ContentResolver resolver = getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, eventID + "Code" + ".jpg");
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                Objects.requireNonNull(fos);
                Toast.makeText(QRGen.this, "Image saved to" + imageUri.getPath(), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        findViewById(R.id.share_btn2).setOnClickListener(v -> {
            OutputStream fos;
            try {
                qrgEncoder = new QRGEncoder("promo" + eventID, null, QRGContents.Type.TEXT, 200);
                Bitmap bitmap = qrgEncoder.getBitmap(0);
                ContentResolver resolver = getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, eventID + "Code" + ".jpg");
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                Objects.requireNonNull(fos);
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setType("image/png");
                startActivity(intent);
                // Delete the image after sharing (maybe unneeded)
                // resolver.delete(imageUri, null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        findViewById(R.id.print_btn).setOnClickListener(v -> {
            Intent intent = new Intent(this, PrintDisplay.class);
            intent.putExtra("contents", eventID);
            startActivity(intent);
        });
    }
}