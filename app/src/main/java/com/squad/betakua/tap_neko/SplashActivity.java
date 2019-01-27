package com.squad.betakua.tap_neko;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.squad.betakua.tap_neko.nfc.NFCActivity;

public class SplashActivity extends AppCompatActivity {

    private Button audioRecorderButton;
    private Button barcodeScannerButton;
    private boolean isPatient = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);

        if (isPatient) {
            Intent patientIntent = new Intent(getApplicationContext(), PatientActivity.class);
            startActivity(patientIntent);
        } else {
            Intent pharmacistIntent = new Intent(getApplicationContext(), PharmacistActivity.class);
            startActivity(pharmacistIntent);
        }
    }
}
