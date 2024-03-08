package com.example.holosproject;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.OutputStream;
import java.util.Objects;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;

public class QRGen extends AppCompatActivity {
// see https://github.com/androidmads/QRGenerator
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

        Button genButton = findViewById(R.id.qr_btn);
        Button saveButton = findViewById(R.id.save_btn);
        ImageView QRView = findViewById(R.id.QRView);
        TextInputEditText editText = findViewById(R.id.qrText);

// GENERATE QR CODE FUNCTIONALITY
        genButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QRGEncoder qrgEncoder = new QRGEncoder(editText.getText().toString(), null, QRGContents.Type.TEXT, 250);
                try {
                    Bitmap bitmap = qrgEncoder.getBitmap(0);
                    QRView.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // SAVING QR CODE FUNCTIONALITY

        findViewById(R.id.save_btn).setOnClickListener(v -> {
                OutputStream fos;
                try {
                    QRGEncoder qrgEncoder = new QRGEncoder(editText.getText().toString(), null, QRGContents.Type.TEXT, 200);
                    Bitmap bitmap = qrgEncoder.getBitmap(0);
                    ContentResolver resolver = getContentResolver();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, editText.getText().toString() + "Code" + ".jpg");
                    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
                    Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                    fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    Objects.requireNonNull(fos);
                    Toast.makeText(QRGen.this, "Image saved", Toast.LENGTH_LONG).show();
                    editText.setText(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
        });
    }
}