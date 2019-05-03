package com.squad.betakua.tap_neko.barcode;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squad.betakua.tap_neko.R;

import static com.squad.betakua.tap_neko.PharmacistActivity.BARCODE_KEY;

/**
 * Created by sherryuan on 2019-01-26.
 */

public class BarcodeScannerActivity extends AppCompatActivity {
    TextView tvCardText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);

        tvCardText = findViewById(R.id.tv_code_text);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startQRScanner();
            }
        }, 1500);
    }

    private void startQRScanner() {
        new IntentIntegrator(this).initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                data.putExtra(BARCODE_KEY, result.getContents());
                Log.e("awefawef", result.getContents());
                setResult(RESULT_OK, data);
                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
