package com.squad.betakua.tap_neko;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button audioRecorderButton;
    private Button barcodeScannerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initAudioRecorderButton();
        initBarcodeScannerButton();
    }

    private void initAudioRecorderButton() {
        audioRecorderButton = findViewById(R.id.audio_recorder_button);
        audioRecorderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent audioRecorderIntent = new Intent(getApplicationContext(), AudioRecorderActivity.class);
                startActivity(audioRecorderIntent);
            }
        });
    }

    private void initBarcodeScannerButton() {
        barcodeScannerButton = findViewById(R.id.barcode_scanner_button);
        barcodeScannerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent barcodeScannerIntent = new Intent(getApplicationContext(), BarcodeScannerActivity.class);
                startActivity(barcodeScannerIntent);
            }
        });
    }
}
