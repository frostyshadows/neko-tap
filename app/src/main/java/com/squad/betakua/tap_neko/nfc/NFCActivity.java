package com.squad.betakua.tap_neko.nfc;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.squad.betakua.tap_neko.Constants;
import com.squad.betakua.tap_neko.R;

public class NFCActivity extends AppCompatActivity {
    public static final int NFC_REQ_CODE = 123;
    public static final String NFC_ID_KEY = "nfc_id";
    Button nfcDemoBtn;

    TextView text;
    TextView textSuccess;
    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    static final String TAG = "TTS";
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
            Intent data = new Intent();
            data.putExtra(NFC_ID_KEY, "321");
            setResult(RESULT_OK, data);
            finish();
            return;
        }

        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, this.getClass())
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
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
        Intent data = new Intent();
        data.putExtra(NFC_ID_KEY, Constants.DEMO_NFC_CODE.toString());
        setResult(RESULT_OK, data);
        displaySuccessAnimation();
    }

    private void resolveIntent(Intent intent) {
        String action = intent.getAction();

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
            Intent data = new Intent();
            data.putExtra(NFC_ID_KEY, Utils.toHex(id));
            setResult(RESULT_OK, data);
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
                finish();
            }
        }, 2000);
    }

}
