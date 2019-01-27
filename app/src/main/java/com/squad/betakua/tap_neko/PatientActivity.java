package com.squad.betakua.tap_neko;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.squad.betakua.tap_neko.nfc.NFCActivity;

/**
 * Created by sherryuan on 2019-01-26.
 */

public class PatientActivity extends AppCompatActivity {

    private Button stopButton;
    private Button playButton;

    private boolean hasAudio = false;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private String outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);
        initAudioPlayer();
        initPlayButton();
        initStopButton();
    }

    private void initAudioPlayer() {
        try {
            mediaPlayer.setDataSource(outputFile);
            mediaPlayer.prepare();
            mediaPlayer.start();
            Toast.makeText(getApplicationContext(), "Playing Audio", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            // make something
            e.printStackTrace();
        }
    }

    private void initPlayButton() {
        playButton = findViewById(R.id.play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    mediaPlayer.start();
                    Toast.makeText(getApplicationContext(), "Playing Audio", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initStopButton() {
        stopButton = findViewById(R.id.stop_button);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
            }
        });
    }
}
