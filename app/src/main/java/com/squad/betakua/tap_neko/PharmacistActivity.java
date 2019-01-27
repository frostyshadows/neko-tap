package com.squad.betakua.tap_neko;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.squad.betakua.tap_neko.nfc.NFCActivity;

/**
 * Created by sherryuan on 2019-01-26.
 */

public class PharmacistActivity extends AppCompatActivity {

    public static final int BARCODE_REQ_CODE = 100;
    public static final String BARCODE_KEY = "barcode";
    public static final int AUDIO_REQ_CODE = 101;

    private boolean isClient = false;

    private Button audioRecorderButton;
    private boolean hasAudio = false;

    private Button barcodeScannerButton;
    private String barcodeId;
    private boolean hasBarcode = false;

    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacist);
        initAudioRecorderButton();
        initBarcodeScannerButton();
        initSubmitButton();
        refreshSubmitButton();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == BARCODE_REQ_CODE && resultCode == RESULT_OK) {
            barcodeId = data.getStringExtra(BARCODE_KEY);
            hasBarcode = true;
            Toast.makeText(this, barcodeId, Toast.LENGTH_SHORT).show();
            refreshSubmitButton();
        } else if (requestCode == AUDIO_REQ_CODE && resultCode == RESULT_OK) {
            hasAudio = true;
            Toast.makeText(this, "got audio", Toast.LENGTH_SHORT).show();
            refreshSubmitButton();
        }
    }

    private void initAudioRecorderButton() {
        audioRecorderButton = findViewById(R.id.audio_recorder_button);
        audioRecorderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent audioRecorderIntent = new Intent(getApplicationContext(), AudioRecorderActivity.class);
                startActivityForResult(audioRecorderIntent, AUDIO_REQ_CODE);
            }
        });
    }
    private void initBarcodeScannerButton() {
        barcodeScannerButton = findViewById(R.id.barcode_scanner_button);
        barcodeScannerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent barcodeScannerIntent = new Intent(getApplicationContext(), BarcodeScannerActivity.class);
                startActivityForResult(barcodeScannerIntent, BARCODE_REQ_CODE);
            }
        });
    }

    private void initSubmitButton() {
        submitButton = findViewById(R.id.submit_button);
    }

    // submit button should only be enabled if both audio, barcode, and NFC have been prepared
    private void refreshSubmitButton() {
        if (hasAudio && hasBarcode) {
            submitButton.setEnabled(true);
        } else {
            submitButton.setEnabled(false);
        }
    }

    public boolean onBtnClick(View v) {
        int id = v.getId();

        if (id == R.id.nfc_button) {
            Intent intent = new Intent(PharmacistActivity.this, NFCActivity.class);
            startActivity(intent);
        }

        return true;
    }
}
