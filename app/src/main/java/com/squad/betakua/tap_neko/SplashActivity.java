package com.squad.betakua.tap_neko;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.squad.betakua.tap_neko.auth.AuthActivity;
import com.squad.betakua.tap_neko.azure.AzureInterface;
import com.squad.betakua.tap_neko.azure.AzureInterfaceException;
import com.squad.betakua.tap_neko.nfc.NFCPatientActivity;

public class SplashActivity extends AppCompatActivity {

    private static final int WAIT = 1000;

    private Button audioRecorderButton;
    private Button barcodeScannerButton;
    private boolean isPatient = true;
    private boolean bypassAuth = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        View splashScreen = findViewById(R.id.splash_screen);

        // background gradient animation
        ConstraintLayout constraintLayout = findViewById(R.id.splash_screen);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        splashScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    AzureInterface.init(getApplicationContext());
                } catch (AzureInterfaceException e) {
                    e.printStackTrace();
                }

                if (isPatient) {
                    // Intent patientIntent = new Intent(getApplicationContext(), NFCPatientActivity.class); //NFCPatientActivity.class
                    Intent patientIntent = new Intent(getApplicationContext(), PatientActivity.class); //NFCPatientActivity.class
                    startActivity(patientIntent);
                } else {
                    if (bypassAuth) {
                        Intent pharmIntent = new Intent(getApplicationContext(), PharmacistActivity.class);
                        startActivity(pharmIntent);
                    } else {
                        Intent authIntent = new Intent(getApplicationContext(), AuthActivity.class);
                        startActivity(authIntent);
                    }
                }
            }
        });

        // new Handler().postDelayed(new Runnable() {
        //     @Override
        //     public void run() {
        //         try {
        //             AzureInterface.init(getApplicationContext());
        //         } catch (AzureInterfaceException e) {
        //             e.printStackTrace();
        //         }
        //
        //         if (isPatient) {
        //             Intent patientIntent = new Intent(getApplicationContext(), NFCPatientActivity.class);
        //             startActivity(patientIntent);
        //         } else {
        //             Intent pharmacistIntent = new Intent(getApplicationContext(), PharmacistActivity.class);
        //             startActivity(pharmacistIntent);
        //         }
        //     }
        // }, WAIT);

    }
}
