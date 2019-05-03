package com.squad.betakua.tap_neko.patientmedrecord;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.squad.betakua.tap_neko.R;

import static com.squad.betakua.tap_neko.nfc.NFCPatientActivity.ADD_MED_RECORD_SPLASH_KEY;

public class AddMedRecordSplash extends AppCompatActivity {
    Button acceptBtn;
    Button rejectBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_med_record_splash);

        acceptBtn = findViewById(R.id.add_med_record_splash_accept);
        rejectBtn = findViewById(R.id.add_med_record_splash_reject);

        acceptBtn.setOnClickListener((View view) -> {
            Intent data = new Intent();
            data.putExtra(ADD_MED_RECORD_SPLASH_KEY, true);
            setResult(RESULT_OK, data);
            finish();
        });

        rejectBtn.setOnClickListener((View view) -> {
            Intent data = new Intent();
            data.putExtra(ADD_MED_RECORD_SPLASH_KEY, false);
            setResult(RESULT_OK, data);
            finish();
        });
    }


}
