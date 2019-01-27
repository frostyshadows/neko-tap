package com.squad.betakua.tap_neko.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.squad.betakua.tap_neko.PharmacistActivity;
import com.squad.betakua.tap_neko.R;

public class AuthActivity extends AppCompatActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        LottieAnimationView lottieView = findViewById(R.id.lottie_auth);
        lottieView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSuccess();
            }
        });
    }

    public void onSuccess() {
        Intent pharmacistActivity = new Intent(getApplicationContext(), PharmacistActivity.class);
        startActivity(pharmacistActivity);
    }
}
