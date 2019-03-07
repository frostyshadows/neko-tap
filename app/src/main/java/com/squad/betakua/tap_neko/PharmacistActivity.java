package com.squad.betakua.tap_neko;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.squad.betakua.tap_neko.audiorecord.AzureSpeechActivity;
import com.squad.betakua.tap_neko.azure.AzureInterface;
import com.squad.betakua.tap_neko.azure.AzureInterfaceException;
import com.squad.betakua.tap_neko.azure.InfoItem;
import com.squad.betakua.tap_neko.barcode.BarcodeScannerActivity;
import com.squad.betakua.tap_neko.nfc.NFCActivity;
import com.squad.betakua.tap_neko.utils.Utils;

import static com.squad.betakua.tap_neko.nfc.NFCActivity.NFC_ID_KEY;
import static com.squad.betakua.tap_neko.nfc.NFCActivity.NFC_REQ_CODE;

/**
 * Created by sherryuan on 2019-01-26.
 */

public class PharmacistActivity extends AppCompatActivity {

    public static final int BARCODE_REQ_CODE = 100;
    public static final String BARCODE_KEY = "barcode";
    public static final int AUDIO_REQ_CODE = 101;
    public static final String AUDIO_REQ_KEY = "audio_record";
    public static final String AUDIO_TRANSCRIPT_KEY = "audio_transcript";

    private TableRow audioRecorderButton;
    private String transcript;
    private boolean hasAudio = false;

    private TableRow barcodeScannerButton;
    private String barcodeId;
    private boolean hasBarcode = false;

    private TableRow nfcButton;
    private ImageButton submitButton;

    private String nfcId;
    private String fileId;
    private boolean hasNfcId = false;

    private TextView textBarcode;
    private TextView textNFC;
    private TextView textAudio;

    private LottieAnimationView lottieBarcode;
    private LottieAnimationView lottieNFC;
    private LottieAnimationView lottieAudio;

    private static final String MOCK_YOUTUBE_URL = "https://www.youtube.com/watch?v=uGkbreu169Q";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacist);
        initAudioRecorderButton();
        initBarcodeScannerButton();
        initNfcButton();
        initSubmitButton();
        refreshSubmitButton();

        textBarcode = findViewById(R.id.scan_text);
        textNFC = findViewById(R.id.nfc_text);
        textAudio = findViewById(R.id.audio_text);

        lottieBarcode = findViewById(R.id.check_barcode);
        lottieNFC = findViewById(R.id.check_nfc);
        lottieAudio = findViewById(R.id.check_audio);
        lottieBarcode.setProgress(0f);
        lottieNFC.setProgress(0f);
        lottieAudio.setProgress(0f);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == BARCODE_REQ_CODE && resultCode == RESULT_OK) {
            barcodeId = data.getStringExtra(BARCODE_KEY);
            hasBarcode = true;

            // change colors
            textBarcode.setTextColor(Color.parseColor("#FFFFFF"));
            barcodeScannerButton.setBackgroundColor(Color.parseColor("#6dcc5b"));
            lottieBarcode.setMaxProgress(0.5f);
            lottieBarcode.playAnimation();

            refreshSubmitButton();
        } else if (requestCode == AUDIO_REQ_CODE && resultCode == RESULT_OK) {
            // get audio transcript
            transcript = data.getStringExtra(AUDIO_TRANSCRIPT_KEY);
            hasAudio = true;

            // change colors
            textAudio.setTextColor(Color.parseColor("#FFFFFF"));
            audioRecorderButton.setBackgroundColor(Color.parseColor("#6dcc5b"));
            lottieAudio.setMaxProgress(0.5f);
            lottieAudio.playAnimation();

            refreshSubmitButton();
        } else if (requestCode == NFC_REQ_CODE && resultCode == RESULT_OK) {
            // get NFC id
            nfcId = data.getStringExtra(NFC_ID_KEY);
            fileId = Utils.nfcToFileName(nfcId);
            hasNfcId = true;

            // change colors
            textNFC.setTextColor(Color.parseColor("#FFFFFF"));
            nfcButton.setBackgroundColor(Color.parseColor("#6dcc5b"));
            lottieNFC.setMaxProgress(0.5f);
            lottieNFC.playAnimation();

            refreshSubmitButton();
        }
    }

    private void initAudioRecorderButton() {
        audioRecorderButton = findViewById(R.id.audio_recorder_button);
        audioRecorderButton.setOnClickListener((View view) -> {
            Intent audioRecorderIntent = new Intent(getApplicationContext(), AzureSpeechActivity.class);
            audioRecorderIntent.putExtra("nfcId", nfcId);
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
                ListenableFuture<InfoItem> infoItemsFuture = AzureInterface.getInstance().checkIfInfoItemRowExists(nfcId);

                Futures.addCallback(infoItemsFuture, new FutureCallback<InfoItem>() {
                    public void onSuccess(InfoItem infoItem) {
                        // if row already exists, update it
                        updateInfoItemToTable();
                        Log.e("HERE00", "SUCCESS");
                    }

                    public void onFailure(Throwable t) {
                        // if row doesn't exist, add it
                        insertInfoItemToTable();
                        Log.e("ERROR", t.toString());
                    }
                });
            } catch (AzureInterfaceException e) {
                Log.e("ERROR:", e.toString());
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

    private void insertInfoItemToTable() throws AzureInterfaceException {
        try {
            ListenableFuture<InfoItem> infoItemsFuture = AzureInterface.getInstance().writeInfoItem(nfcId, barcodeId, transcript, MOCK_YOUTUBE_URL);

            Futures.addCallback(infoItemsFuture, new FutureCallback<InfoItem>() {
                public void onSuccess(InfoItem infoItem) {
                    // TODO: signal success in UI
                }

                public void onFailure(Throwable t) {
                    // TODO: signal failure in UI
                    t.printStackTrace();
                }
            });
        } catch (AzureInterfaceException e) {
            Log.e("ERROR:", e.toString());
        }
    }

    private void updateInfoItemToTable() {
        try {
            ListenableFuture<InfoItem> infoItemsFuture = AzureInterface.getInstance().updateInfoItem(nfcId, barcodeId, transcript, MOCK_YOUTUBE_URL);

            Futures.addCallback(infoItemsFuture, new FutureCallback<InfoItem>() {
                public void onSuccess(InfoItem infoItem) {
                    // TODO: signal success in UI
                }

                public void onFailure(Throwable t) {
                    // TODO: signal failure in UI
                    t.printStackTrace();
                }
            });
        } catch (AzureInterfaceException e) {
            Log.e("ERROR:", e.toString());
        }
    }

}
