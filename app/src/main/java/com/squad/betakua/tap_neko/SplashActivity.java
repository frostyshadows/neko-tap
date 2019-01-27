package com.squad.betakua.tap_neko;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.squad.betakua.tap_neko.azure.AzureInterface;
import com.squad.betakua.tap_neko.azure.AzureInterfaceException;
import com.squad.betakua.tap_neko.nfc.NFCActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int WAIT = 1000;

    private Button audioRecorderButton;
    private Button barcodeScannerButton;
    private boolean isPatient = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    AzureInterface.init(getApplicationContext());
                } catch (AzureInterfaceException e) {
                    e.printStackTrace();
                }

                if (isPatient) {
                    Intent patientIntent = new Intent(getApplicationContext(), PatientActivity.class);
                    startActivity(patientIntent);
                } else {
                    Intent pharmacistIntent = new Intent(getApplicationContext(), PharmacistActivity.class);
                    startActivity(pharmacistIntent);
                }
            }
        }, WAIT);

    }
}
