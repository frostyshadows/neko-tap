package com.squad.betakua.tap_neko;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.model.KeyPath;
import com.airbnb.lottie.value.LottieValueCallback;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.squad.betakua.tap_neko.audiorecord.AzureSpeechActivity;
import com.squad.betakua.tap_neko.azure.AzureInterface;
import com.squad.betakua.tap_neko.azure.AzureInterfaceException;
import com.squad.betakua.tap_neko.azure.InfoItem;
import com.squad.betakua.tap_neko.barcode.BarcodeScannerActivity;
import com.squad.betakua.tap_neko.nfc.NFCActivity;
import com.squad.betakua.tap_neko.notifications.RefillReminder;
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
    public static final String AUDIO_TRANSLATE_KEY = "audio_translate";
    public static final int REFILL_REMINDER_CODE = 100;
    public static final String REFILL_REMINDER_KEY = "refill_reminder";

    private TableRow audioRecorderButton;
    private String transcript;
    private String translation;
    private boolean hasAudio = false;

    private TableRow barcodeScannerButton;
    private String barcodeId;
    private boolean hasBarcode = false;

    private TableRow nfcButton;
    private Button submitButton;
    private LottieAnimationView submitButtonProgress;

    private TableRow refillButton;
    private String refillId;
    private String refillDate;
    private boolean hasReminder = false;

    private String nfcId;
    private String fileId;
    private boolean hasNfcId = false;

    private TextView textBarcode;
    private TextView textNFC;
    private TextView textAudio;
    private TextView textRefill;

    private LottieAnimationView lottieBarcode;
    private LottieAnimationView lottieNFC;
    private LottieAnimationView lottieAudio;
    private LottieAnimationView lottieRefill;

    private static final String MOCK_YOUTUBE_URL = "https://www.youtube.com/watch?v=uGkbreu169Q";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacist);
        barcodeScannerButton = findViewById(R.id.barcode_scanner_button);
        nfcButton = findViewById(R.id.nfc_button);
        audioRecorderButton = findViewById(R.id.audio_recorder_button);
        refillButton = findViewById(R.id.refill_reminder_button);
        textBarcode = findViewById(R.id.scan_text);
        textNFC = findViewById(R.id.nfc_text);
        textAudio = findViewById(R.id.audio_text);
        textRefill = findViewById(R.id.refill_text);

        initAudioRecorderButton();
        initBarcodeScannerButton();
        initNfcButton();
        initSubmitButton();
        initCheckboxAnimations();
        refreshSubmitButton();
        initRefillReminderButton();

        nfcButton.setEnabled(false);
        audioRecorderButton.setEnabled(false);
        refillButton.setEnabled(false);

        nfcButton.setBackgroundColor(getResources().getColor(R.color.superLightGrey));
        audioRecorderButton.setBackgroundColor(getResources().getColor(R.color.superLightGrey));
        refillButton.setBackgroundColor(getResources().getColor(R.color.superLightGrey));
    }

    private void initCheckboxAnimations() {
        lottieBarcode = findViewById(R.id.check_barcode);
        lottieNFC = findViewById(R.id.check_nfc);
        lottieAudio = findViewById(R.id.check_audio);
        lottieRefill = findViewById(R.id.check_refill);

        lottieBarcode.setProgress(0f);
        lottieNFC.setProgress(0f);
        lottieAudio.setProgress(0f);
        lottieRefill.setProgress(0f);

        lottieBarcode.addValueCallback(
                new KeyPath("**"),
                LottieProperty.COLOR,
                new LottieValueCallback<>(Color.WHITE));
        lottieNFC.addValueCallback(
                new KeyPath("**"),
                LottieProperty.COLOR,
                new LottieValueCallback<>(Color.WHITE));
        lottieAudio.addValueCallback(
                new KeyPath("**"),
                LottieProperty.COLOR,
                new LottieValueCallback<>(Color.WHITE));
        lottieRefill.addValueCallback(
                new KeyPath("**"),
                LottieProperty.COLOR,
                new LottieValueCallback<>(Color.WHITE));
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

            nfcButton.setEnabled(true);
            nfcButton.setBackgroundColor(getResources().getColor(R.color.white));

            refreshSubmitButton();
        } else if (requestCode == AUDIO_REQ_CODE && resultCode == RESULT_OK) {
            // get audio transcript
            transcript = data.getStringExtra(AUDIO_TRANSCRIPT_KEY);
            translation = data.getStringExtra(AUDIO_TRANSLATE_KEY);
            hasAudio = true;

            // change colors
            textAudio.setTextColor(Color.parseColor("#FFFFFF"));
            audioRecorderButton.setBackgroundColor(Color.parseColor("#6dcc5b"));
            lottieAudio.setMaxProgress(0.5f);
            lottieAudio.playAnimation();

            refillButton.setEnabled(true);
            refillButton.setBackgroundColor(getResources().getColor(R.color.white));

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

            audioRecorderButton.setEnabled(true);
            audioRecorderButton.setBackgroundColor(getResources().getColor(R.color.white));

            refreshSubmitButton();
        } else if (requestCode == REFILL_REMINDER_CODE && resultCode == RESULT_OK){
            //get date
            refillId = data.getStringExtra(REFILL_REMINDER_KEY);
            //Set refill date
            hasReminder = true;

            // change colors
            textRefill.setTextColor(Color.parseColor("#FFFFFF"));
            refillButton.setBackgroundColor(Color.parseColor("#6dcc5b"));
            lottieRefill.setMaxProgress(0.5f);
            lottieRefill.playAnimation();
        }
    }

    private void initAudioRecorderButton() {
        audioRecorderButton.setOnClickListener((View view) -> {
            Intent audioRecorderIntent = new Intent(getApplicationContext(), AzureSpeechActivity.class);
            audioRecorderIntent.putExtra("nfcId", nfcId);
            startActivityForResult(audioRecorderIntent, AUDIO_REQ_CODE);
        });
    }

    private void initBarcodeScannerButton() {
        barcodeScannerButton.setOnClickListener((View view) -> {
            Intent barcodeScannerIntent = new Intent(getApplicationContext(), BarcodeScannerActivity.class);
            startActivityForResult(barcodeScannerIntent, BARCODE_REQ_CODE);
        });
    }

    private void initRefillReminderButton(){
        refillButton.setOnClickListener((View view) -> {
            Intent refillReminderIntent = new Intent(getApplicationContext(), RefillReminder.class);
            startActivityForResult(refillReminderIntent, REFILL_REMINDER_CODE);
        });
    }

    private void initSubmitButton() {
        submitButtonProgress = findViewById(R.id.submit_button_progress);
        submitButtonProgress.setVisibility(View.INVISIBLE);
        submitButtonProgress.playAnimation();
        // change lottie color to white
        submitButtonProgress.addValueCallback(
                new KeyPath("**"),
                LottieProperty.COLOR,
                new LottieValueCallback<>(Color.WHITE));

        submitButton = findViewById(R.id.submit_button);
        submitButton.setVisibility(View.VISIBLE);

        submitButton.setOnClickListener((View view) -> {
            onSubmitProgress();

            try {
                ListenableFuture<InfoItem> infoItemsFuture = AzureInterface.getInstance().checkIfInfoItemRowExists(nfcId);

                Futures.addCallback(infoItemsFuture, new FutureCallback<InfoItem>() {
                    public void onSuccess(InfoItem infoItem) {
                        // if row already exists, update it
                        updateInfoItemToTable();
                    }

                    public void onFailure(Throwable t) {
                        // if row doesn't exist, add it
                        insertInfoItemToTable();
                    }
                });
            } catch (AzureInterfaceException e) {
                onSubmitError();
                e.printStackTrace();
            }
        });

    }

    // submit button should only be enabled if both audio, barcode, and NFC have been prepared
    private void refreshSubmitButton() {
        if (hasAudio && hasBarcode && hasNfcId && hasReminder) {
            submitButton.setEnabled(true);
        } else {
            submitButton.setEnabled(false);
        }
    }

    public void initNfcButton() {
        nfcButton.setOnClickListener((View view) -> {
            Intent intent = new Intent(PharmacistActivity.this, NFCActivity.class);
            startActivityForResult(intent, NFC_REQ_CODE);
        });
    }

    private void insertInfoItemToTable() {
        String productName = demoGenerateProductName();
        String url = demoGenerateUrl();
        String webUrl = demoGenerateWebUrl();
        String pharmacyPhone = "1-800-867-1389";
        String pharmacyName = "Shoppers Drug Mart #2323";
        String pharmacist = "John Lee";
        String translated = demoGenerateTranslated();
        String reminder = "";

        try {
            ListenableFuture<InfoItem> infoItemsFuture = AzureInterface.getInstance()
                    .writeInfoItem(nfcId,
                            barcodeId,
                            productName,
                            transcript,
                            url,
                            webUrl,
                            pharmacyPhone,
                            pharmacyName,
                            pharmacist,
                            translated,
                            reminder
                    );

            Futures.addCallback(infoItemsFuture, new FutureCallback<InfoItem>() {
                public void onSuccess(InfoItem infoItem) {
                    onSubmitSuccess();
                }

                public void onFailure(Throwable t) {
                    onSubmitError();
                    t.printStackTrace();
                }
            });
        } catch (AzureInterfaceException e) {
            onSubmitError();
            e.printStackTrace();
        }
    }

    private void updateInfoItemToTable() {
        String productName = demoGenerateProductName();
        String url = demoGenerateUrl();
        String webUrl = demoGenerateWebUrl();
        String pharmacyPhone = "1-800-867-1389";
        String pharmacyName = "Shoppers Drug Mart #2323";
        String pharmacist = "John Lee";
        String translated = demoGenerateTranslated();
        String reminder = "";

        try {
            ListenableFuture<InfoItem> infoItemsFuture = AzureInterface.getInstance()
                    .updateInfoItem(nfcId,
                            barcodeId,
                            productName,
                            transcript,
                            url,
                            webUrl,
                            pharmacyPhone,
                            pharmacyName,
                            pharmacist,
                            translated,
                            reminder
                    );

            Futures.addCallback(infoItemsFuture, new FutureCallback<InfoItem>() {
                public void onSuccess(InfoItem infoItem) {
                    onSubmitSuccess();
                }

                public void onFailure(Throwable t) {
                    onSubmitError();
                    t.printStackTrace();
                }
            });
        } catch (AzureInterfaceException e) {
            onSubmitError();
            e.printStackTrace();
        }
    }

    private void onSubmitProgress() {
        submitButton.setText(R.string.pharmacist_submit_button_submitting);
        submitButton.setEnabled(false);
        submitButton.setVisibility(View.INVISIBLE);
        submitButtonProgress.setVisibility(View.VISIBLE);
    }

    private void onSubmitSuccess() {
        submitButton.setText(R.string.pharmacist_submit_button);
        submitButton.setEnabled(true);
        submitButton.setVisibility(View.VISIBLE);
        submitButtonProgress.setVisibility(View.INVISIBLE);
        Toast.makeText(this, R.string.pharmacist_submit_button_success, Toast.LENGTH_SHORT).show();
    }

    private void onSubmitError() {
        submitButton.setText(R.string.pharmacist_submit_button);
        submitButton.setEnabled(true);
        submitButton.setVisibility(View.VISIBLE);
        submitButtonProgress.setVisibility(View.INVISIBLE);
        Toast.makeText(this, R.string.pharmacist_submit_button_error, Toast.LENGTH_SHORT).show();
    }

    private String demoGenerateProductName() {
        if (barcodeId == null || barcodeId.equals("")) {
            return "Aerochamber (Child)";
        }

        Integer id = Character.getNumericValue(barcodeId.charAt(0));

        switch (id) {
            case 0:
            case 1:
                // Methotrexate
                return "Methotrexate 2.5mg Tablets";
            case 2:
            case 3:
                // Humira
                return "Humira 40mg/0.8mL Injection";
            case 4:
            case 5:
                // Spiriva
                return "Spiriva Respimat Inhaler 2.5mcg (60 puffs)";
            case 6:
            case 7:
                // Accucheck
                return "Accucheck Nano Blood Glucose Monitoring Device";
            default:
                // Spacer
                return "Aerochamber (Child)";
        }
    }

    private String demoGenerateUrl() {
        if (barcodeId == null || barcodeId.equals("")) {
            return "ma_cmlU9DxU";
        }

        Integer id = Character.getNumericValue(barcodeId.charAt(0));

        switch (id) {
            case 0:
            case 1:
                // Methotrexate
                return "I07EGu4Z9TU";
            case 2:
            case 3:
                // Humira
                return "e8cS2lwwgeA";
            case 4:
            case 5:
                // Spiriva
                return "ln6zmUHVdfE";
            case 6:
            case 7:
                // Accucheck
                return "pxgyAvKkoc4";
            default:
                // Spacer
                return "ma_cmlU9DxU";
        }
    }

    private String demoGenerateWebUrl() {
        if (barcodeId == null || barcodeId.equals("")) {
            return "https://www.aerochambervhc.com/instructions-for-use/";
        }

        Integer id = Character.getNumericValue(barcodeId.charAt(0));

        switch (id) {
            case 0:
            case 1:
                // Methotrexate
                return "https://www.mayoclinic.org/drugs-supplements/methotrexate-oral-route/proper-use/drg-20084837";
            case 2:
            case 3:
                // Humira
                return "https://www.humira.com/humira-complete/injection";
            case 4:
            case 5:
                // Spiriva
                return "https://www.spiriva.com/asthma/how-to-use";
            case 6:
            case 7:
                // Accucheck
                return "https://www.accu-chek.ca/en/diabetescare/when-why-how-test";
            default:
                // Spacer
                return "https://www.aerochambervhc.com/instructions-for-use/";
        }
    }

    private String demoGenerateTranslated() {
        return translation;
        // alendronate
        // return "\"这种药物用于治疗和预防骨质疏松症，骨质疏松，骨骼变薄，变弱。它通过防止骨质破坏和增加骨密度（厚度）起作用。\n\n" +
        //         "如果不按照以下说明服用，阿仑膦酸盐可能无法正常工作，可能会损坏食道或导致口腔溃疡。如果您不理解这些说明，请告诉您的医生。\n\n" +
        //         "早上起床后，你必须服用阿仑膦酸钠，然后才能吃或喝任何东西。不要在睡前服用阿仑膦酸钠。\n\n";
    }

}
