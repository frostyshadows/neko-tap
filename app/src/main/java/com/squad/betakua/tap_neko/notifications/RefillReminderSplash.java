package com.squad.betakua.tap_neko.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.squad.betakua.tap_neko.R;

import static com.squad.betakua.tap_neko.nfc.NFCPatientActivity.REFILL_SPLASH_KEY;

public class RefillReminderSplash extends AppCompatActivity {
    Button acceptBtn;
    Button rejectBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refill_reminder_splash);

        acceptBtn = findViewById(R.id.refill_reminder_splash_button_accept);
        rejectBtn = findViewById(R.id.refill_reminder_splash_button_reject);

        acceptBtn.setOnClickListener((View view) -> {
            Intent data = new Intent();
            data.putExtra(REFILL_SPLASH_KEY, true);
            setResult(RESULT_OK, data);
            finish();
        });

        rejectBtn.setOnClickListener((View view) -> {
            Intent data = new Intent();
            data.putExtra(REFILL_SPLASH_KEY, false);
            setResult(RESULT_OK, data);
            finish();
        });
    }
}
