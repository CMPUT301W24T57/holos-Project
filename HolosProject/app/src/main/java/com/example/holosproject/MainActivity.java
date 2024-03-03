package com.example.holosproject;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;

import com.google.zxing.client.android.Intents;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class MainActivity extends AppCompatActivity {
    Button scanButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scanButton = findViewById(R.id.scan_btn);
        scanButton.setOnClickListener(v-> {
            scanQRCode();
        });
    }

    private void scanQRCode() { // basic QR code scan
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to turn camera flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result->{ //basic popup after scanning to test things
       if(result.getContents() != null) {
           AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
           builder.setTitle("Result");
           builder.setMessage(result.getContents());
           builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { // dismisses the popup
               @Override
               public void onClick(DialogInterface dialog, int which) {
                   dialog.dismiss();
               }
           }).show();
       }
    });
}