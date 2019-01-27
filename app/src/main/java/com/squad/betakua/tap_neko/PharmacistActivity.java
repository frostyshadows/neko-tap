package com.squad.betakua.tap_neko;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.squad.betakua.tap_neko.azure.AzureInterface;
import com.squad.betakua.tap_neko.azure.AzureInterfaceException;
import com.squad.betakua.tap_neko.nfc.NFCActivity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static com.squad.betakua.tap_neko.nfc.NFCActivity.NFC_ID_KEY;
import static com.squad.betakua.tap_neko.nfc.NFCActivity.NFC_REQ_CODE;

/**
 * Created by sherryuan on 2019-01-26.
 */

public class PharmacistActivity extends AppCompatActivity {

    public static final int BARCODE_REQ_CODE = 100;
    public static final String BARCODE_KEY = "barcode";
    public static final int AUDIO_REQ_CODE = 101;

    private boolean isClient = false;

    private Button audioRecorderButton;
    private String outputFile;
    private InputStream audioStream;
    private boolean hasAudio = false;

    private Button barcodeScannerButton;
    private String barcodeId;
    private boolean hasBarcode = false;

    private Button submitButton;

    private Button nfcButton;
    private String nfcId;
    private boolean hasNfcId = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacist);
        initAudioRecorderButton();
        initBarcodeScannerButton();
        initNfcButton();
        initSubmitButton();
        refreshSubmitButton();
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BARCODE_REQ_CODE && resultCode == RESULT_OK) {
            // get barcode
            barcodeId = data.getStringExtra(BARCODE_KEY);
            hasBarcode = true;
            // Toast.makeText(this, barcodeId, Toast.LENGTH_SHORT).show();
            refreshSubmitButton();
        } else if (requestCode == AUDIO_REQ_CODE && resultCode == RESULT_OK) {
            // get audio
            hasAudio = true;
            Toast.makeText(this, "got audio", Toast.LENGTH_SHORT).show();
            refreshSubmitButton();
        } else if (requestCode == NFC_REQ_CODE && resultCode == RESULT_OK) {
            // get NFC id
            nfcId = data.getStringExtra(NFC_ID_KEY);
            hasNfcId = true;
            refreshSubmitButton();
        }
    }

    private void initAudioRecorderButton() {
        audioRecorderButton = findViewById(R.id.audio_recorder_button);
        audioRecorderButton.setOnClickListener((View view) -> {
            Intent audioRecorderIntent = new Intent(getApplicationContext(), AudioRecorderActivity.class);
            startActivityForResult(audioRecorderIntent, AUDIO_REQ_CODE);
        });
    }

    private void initBarcodeScannerButton() {
        barcodeScannerButton = findViewById(R.id.barcode_scanner_button);
        barcodeScannerButton.setOnClickListener((View view) -> {
            Intent barcodeScannerIntent = new Intent(getApplicationContext(), BarcodeScannerActivity.class);
            startActivityForResult(barcodeScannerIntent, BARCODE_REQ_CODE);
        });
    }

    private void initSubmitButton() {
        submitButton = findViewById(R.id.submit_button);

        submitButton.setOnClickListener((View view) -> {
            try {
                AzureInterface.getInstance().uploadAudio(nfcId, new FileInputStream(outputFile), -1);
                AzureInterface.getInstance().writeInfoItem(nfcId, barcodeId, "transcript", "www.google.com");
            } catch (AzureInterfaceException | FileNotFoundException e) {
                e.printStackTrace();
            }

        });
    }

    // submit button should only be enabled if both audio, barcode, and NFC have been prepared
    private void refreshSubmitButton() {
        if (hasAudio && hasBarcode && hasNfcId) {
            submitButton.setEnabled(true);
        } else {
            submitButton.setEnabled(false);
        }
    }

    public void initNfcButton() {
        nfcButton = findViewById(R.id.nfc_button);

        nfcButton.setOnClickListener((View view) -> {
            Intent intent = new Intent(PharmacistActivity.this, NFCActivity.class);
            startActivityForResult(intent, NFC_REQ_CODE);
        });
    }
}
