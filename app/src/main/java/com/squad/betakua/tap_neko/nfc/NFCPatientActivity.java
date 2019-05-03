package com.squad.betakua.tap_neko.nfc;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.squad.betakua.tap_neko.Constants;
import com.squad.betakua.tap_neko.PatientActivity;
import com.squad.betakua.tap_neko.R;
import com.squad.betakua.tap_neko.azure.AzureInterface;
import com.squad.betakua.tap_neko.azure.AzureInterfaceException;
import com.squad.betakua.tap_neko.azure.InfoItem;
import com.squad.betakua.tap_neko.notifications.RefillReminderSplash;
import com.squad.betakua.tap_neko.patientmedrecord.AddMedRecordSplash;
import com.squad.betakua.tap_neko.patientmedrecord.PatientMedRecord;

import java.util.Locale;

public class NFCPatientActivity extends AppCompatActivity {
    public static final int NFC_REQ_CODE = 123;
    public static final String NFC_ID_KEY = "nfc_id";
    public static final int REFILL_SPLASH_REQ_CODE = 124;
    public static final String REFILL_SPLASH_KEY = "refill_splash_id";
    public static final int ADD_MED_RECORD_SPLASH_REQ_CODE = 125;
    public static final String ADD_MED_RECORD_SPLASH_KEY = "add_med_record_splash_id";
    Button nfcDemoBtn;
    String nfcId;
    String barcodeId;
    String productName;
    TextView text;
    TextView textSuccess;
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    TextToSpeech mTts;
    LottieAnimationView nfcAnimation;
    LottieAnimationView checkAnimation;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);

        text = findViewById(R.id.nfc_text);
        text.setText("Tap the medicine cap or device tag!");

        textSuccess = findViewById(R.id.nfc_text_success);
        textSuccess.setVisibility(View.GONE);

        nfcAnimation = findViewById(R.id.lottie_nfc);
        checkAnimation = findViewById(R.id.lottie_nfc_success);
        checkAnimation.setVisibility(View.GONE);

        // DEMO
        nfcDemoBtn = findViewById(R.id.nfc_demo_btn);
        nfcDemoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                demoNFCCallback();
            }
        });
        if (Constants.IS_DEMO) {
            nfcDemoBtn.setVisibility(View.VISIBLE);
        }

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "No NFC", Toast.LENGTH_SHORT).show();
            Intent patientIntent = new Intent(getApplicationContext(), PatientActivity.class);
            patientIntent.putExtra(NFC_ID_KEY, "123");
            startActivity(patientIntent);
            return;
        }

        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, this.getClass())
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);


        mTts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    mTts.setSpeechRate(0.7f);

                    int result = mTts.setLanguage(Locale.ENGLISH);
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(getApplicationContext(), "This language is not supported", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.v("TTS", "onInit succeeded");
                        speak("Please tap your phone against your prescription bottle cap.");
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Initialization failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REFILL_SPLASH_REQ_CODE && resultCode == RESULT_OK) {
            boolean acceptsRefillNotification = data.getBooleanExtra(REFILL_SPLASH_KEY, false);
            Log.e("--- INTENT ----", "result is: " + acceptsRefillNotification);
            startPatientIntent();
        } else if (requestCode == ADD_MED_RECORD_SPLASH_REQ_CODE && resultCode == RESULT_OK) {
            boolean acceptsAddMedRecord = data.getBooleanExtra(ADD_MED_RECORD_SPLASH_KEY, false);
            Log.e("--- INTENT ----", "result is: " + acceptsAddMedRecord);
            startPatientIntent();
        }
    }

    private void speak(String s) {
        Bundle bundle = new Bundle();
        bundle.putInt(TextToSpeech.Engine.KEY_PARAM_STREAM, AudioManager.STREAM_MUSIC);
        // mTts.speak(s, TextToSpeech.QUEUE_FLUSH, bundle, null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (nfcAdapter != null) {
            if (!nfcAdapter.isEnabled())
                showWirelessSettings();

            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }

    private void showWirelessSettings() {
        Toast.makeText(this, "You need to enable NFC", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
        startActivity(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        resolveIntent(intent);
    }

    private void demoNFCCallback() {
        nfcId = Constants.DEMO_NFC_CODE.toString();
        // Intent data = new Intent();
        // data.putExtra(NFC_ID_KEY, Constants.DEMO_NFC_CODE);
        // setResult(RESULT_OK, data);
        displaySuccessAnimation();
    }

    private void resolveIntent(Intent intent) {
        String action = intent.getAction();

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs;

            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];

                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }

            } else {
                byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
                Intent data = new Intent();

                // TODO: check with Azure if the nfcID exists
                // if so, then proceed as usual
                // otherwise, display a message like "nfc tag unrecognized" and try again

                nfcId = Utils.toHex(id);
                data.putExtra(NFC_ID_KEY, nfcId);
                setResult(NFC_REQ_CODE, data);
            }

            displayMsgs();
        }
    }

    private void displayMsgs() {
        // Play a noise
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, ToneGenerator.MAX_VOLUME);
        toneG.startTone(ToneGenerator.TONE_CDMA_ANSWER, 200); //200 is duration in ms

        // Vibrate phone
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(200);
        }

        displaySuccessAnimation();
    }

    private void displaySuccessAnimation() {
        nfcAnimation.setVisibility(View.GONE);
        text.setVisibility(View.GONE);
        checkAnimation.setVisibility(View.VISIBLE);
        textSuccess.setVisibility(View.VISIBLE);
        checkAnimation.playAnimation();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkForRefillNotification();
            }
        }, 2000);
    }

    private void checkForRefillNotification() {
        try {
            ListenableFuture<MobileServiceList<InfoItem>> infoItemsFuture = AzureInterface.getInstance().readInfoItem(nfcId);
            Futures.addCallback(infoItemsFuture, new FutureCallback<MobileServiceList<InfoItem>>() {
                public void onSuccess(MobileServiceList<InfoItem> infoItems) {
                    String reminder = infoItems.get(0).getReminder();
                    if (reminder != null && !reminder.equals("")) {
                        barcodeId = infoItems.get(0).getProductID();
                        productName = infoItems.get(0).getProductName();
                        addNewMedRecord();
                        startRefillSplashIntent();
                    } else {
                        startPatientIntent();
                    }
                }

                public void onFailure(Throwable t) {
                    Log.e("HERE11", "fail " + t.toString());
                    t.printStackTrace();
                }
            });
        } catch (AzureInterfaceException e) {
            Log.e("ERROR:", e.toString());
        }
    }

    private void checkForAddMedRecord() {
        // TODO: actually check for the med record here
        startAddMedRecordSplashIntent();

        // try {
        //     ListenableFuture<MobileServiceList<PatientMedRecord>> infoItemsFuture = AzureInterface.getInstance().checkIfPatientMedRecordExists(barcodeId);
        //     Futures.addCallback(infoItemsFuture, new FutureCallback<MobileServiceList<InfoItem>>() {
        //         public void onSuccess(MobileServiceList<InfoItem> infoItems) {
        //             String reminder = infoItems.get(0).getReminder();
        //             if (reminder != null && !reminder.equals("")) {
        //                 barcodeId = infoItems.get(0).getProductID();
        //                 productName = infoItems.get(0).getProductName();
        //
        //                 startRefillSplashIntent();
        //             } else {
        //                 startAddMedRecordSplashIntent();
        //             }
        //         }
        //
        //         public void onFailure(Throwable t) {
        //             Log.e("HERE11", "fail " + t.toString());
        //             t.printStackTrace();
        //         }
        //     });
        // } catch (AzureInterfaceException e) {
        //     Log.e("ERROR:", e.toString());
        // }
    }

    private void startPatientIntent() {
        Intent patientIntent = new Intent(getApplicationContext(), PatientActivity.class);
        patientIntent.putExtra(NFC_ID_KEY, nfcId);
        Log.e("nfcID1", nfcId);
        startActivity(patientIntent);
    }

    private void startRefillSplashIntent() {
        Intent refillSplashIntent = new Intent(getApplicationContext(), RefillReminderSplash.class);
        refillSplashIntent.putExtra("barcodeId", barcodeId);
        refillSplashIntent.putExtra("productName", productName);
        startActivityForResult(refillSplashIntent, REFILL_SPLASH_REQ_CODE);
    }

    private void startAddMedRecordSplashIntent() {
        Intent refillSplashIntent = new Intent(getApplicationContext(), AddMedRecordSplash.class);
        refillSplashIntent.putExtra("barcodeId", barcodeId);
        refillSplashIntent.putExtra("productName", productName);
        startActivityForResult(refillSplashIntent, ADD_MED_RECORD_SPLASH_REQ_CODE);
    }

    private void addNewMedRecord() {
        PatientMedRecord record = new PatientMedRecord();
        record.setId(nfcId);
        Log.e("awefawef", "fff: " + barcodeId);
        record.setProductID(barcodeId);
        record.setRxNumber(barcodeId);
        record.setNfcID(nfcId);
        record.setDirections("Take one tablet once daily."); // TODO: mock
        record.setQuantity("30"); // TODO: mock
        record.setRefills("3"); // TODO: mock

        try {
            AzureInterface.getInstance().writePatientMedRecord(record);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
