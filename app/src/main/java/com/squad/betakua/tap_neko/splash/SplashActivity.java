package com.squad.betakua.tap_neko.splash;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.squad.betakua.tap_neko.PharmacistActivity;
import com.squad.betakua.tap_neko.R;
import com.squad.betakua.tap_neko.auth.AuthActivity;
import com.squad.betakua.tap_neko.azure.AzureInterface;
import com.squad.betakua.tap_neko.azure.AzureInterfaceException;
import com.squad.betakua.tap_neko.nfc.NFCPatientActivity;

public class SplashActivity extends AppCompatActivity {

    private boolean IS_DEV_MODE = true; // add button to access pharmacist interface
    private boolean isPatient = true;
    // private boolean isPatient = false;
    private boolean bypassAuth = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        View splashScreen = findViewById(R.id.splash_screen);

        // Handle dev mode
        Button pharm_app = findViewById(R.id.pharm_app);
        if (!IS_DEV_MODE) pharm_app.setVisibility(View.INVISIBLE);

        // background gradient animation
        AnimationDrawable animationDrawable = (AnimationDrawable) splashScreen.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();

        // Initiate azure interface singleton
        try {
            AzureInterface.init(getApplicationContext());
        } catch (AzureInterfaceException e) {
            e.printStackTrace();
        }
    }

    public void onClick(View v) {
        int id = v.getId();

        // Intent apharmIntent = new Intent(getApplicationContext(), AzureSpeechActivity.class);
        // startActivity(apharmIntent);

        if (id == R.id.pharm_app) {
            Intent pharmIntent = new Intent(getApplicationContext(), PharmacistActivity.class);
            startActivity(pharmIntent);
        } else if (id == R.id.splash_screen) {
            if (isPatient) {
                Intent patientIntent = new Intent(getApplicationContext(), NFCPatientActivity.class); //NFCPatientActivity.class
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
    }
}
