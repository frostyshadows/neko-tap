package com.squad.betakua.tap_neko;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.squad.betakua.tap_neko.nfc.NFCActivity;

public class MainActivity extends AppCompatActivity {

    private Button audioRecorderButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initAudioRecorderButton();
    }

    private void initAudioRecorderButton() {
        audioRecorderButton = findViewById(R.id.audio_recorder_button);
        audioRecorderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent audioRecorderIntent = new Intent(getApplicationContext(), AudioRecorderActivity.class);
                startActivity(audioRecorderIntent);
            }
        });
    }


    public boolean onBtnClick(View v) {
        int id = v.getId();

        if (id == R.id.nfc_button) {
            Intent intent = new Intent(MainActivity.this, NFCActivity.class);
            startActivity(intent);
        }

        return true;
    }
}
