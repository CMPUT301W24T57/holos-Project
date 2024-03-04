package com.example.holosproject;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class QRGen extends AppCompatActivity {
// see https://github.com/androidmads/QRGenerator
private String savePath = Environment.getExternalStorageDirectory().getPath() + "/QRCode/";
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

        genButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QRGEncoder qrgEncoder = new QRGEncoder(editText.getText().toString(), null, QRGContents.Type.TEXT, 200);
                try {
                    Bitmap bitmap = qrgEncoder.getBitmap(0);
                    QRView.setImageBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}