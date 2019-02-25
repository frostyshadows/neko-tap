package com.squad.betakua.tap_neko.audiorecord;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.squad.betakua.tap_neko.R;

import java.io.IOException;

/**
 * Created by sherryuan on 2019-01-26.
 */

public class AudioRecorderActivity extends AppCompatActivity {
    public static final String TRANSCRIPTION_STR_KEY = "transcribed string";
    public static final String TRANSLATION_STR_KEY = "translated string";

    private Button recordButton;
    private Button stopButton;
    private Button playButton;
    private Button saveButton;

    private static int REQUEST_CODE = 24;
    private MediaRecorder audioRecorder;
    private String outputFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recorder);

        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";

        // ask for permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CODE);
        } else {
            initAudioRecorder();
            initRecordButton();
            initStopButton();
            initPlayButton();
            initSaveButton();
            stopButton.setEnabled(false);
            playButton.setEnabled(false);
            saveButton.setEnabled(false);
        }

    }

    private void initAudioRecorder() {
        audioRecorder = new MediaRecorder();
        audioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        audioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        audioRecorder.setOutputFile(outputFile);
    }

    private void initRecordButton() {
        recordButton = findViewById(R.id.record_button);
        recordButton.setOnClickListener((View view) -> {
            try {
                audioRecorder.prepare();
                audioRecorder.start();
            } catch (IllegalStateException | IOException e) {
                e.printStackTrace();
            }
            recordButton.setEnabled(false);
            stopButton.setEnabled(true);
            Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
        });
    }

    private void initStopButton() {
        stopButton = findViewById(R.id.stop_button);
        stopButton.setOnClickListener((View view) -> {
            if (ContextCompat.checkSelfPermission(AudioRecorderActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(AudioRecorderActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            } else {
                audioRecorder.stop();
                audioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                audioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                audioRecorder.setOutputFile(outputFile);
            }

            recordButton.setEnabled(true);
            stopButton.setEnabled(false);
            playButton.setEnabled(true);
            saveButton.setEnabled(true);
            Toast.makeText(getApplicationContext(), "Audio Recorder stopped", Toast.LENGTH_LONG).show();
        });
    }

    private void initPlayButton() {
        playButton = findViewById(R.id.play_button);
        playButton.setOnClickListener((View view) -> {
            MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(outputFile);
                mediaPlayer.prepare();
                mediaPlayer.start();
                Toast.makeText(getApplicationContext(), "Playing Audio", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                // make something
                e.printStackTrace();
            }
        });
    }

    private void initSaveButton() {
        saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener((View view) -> {
            setResult(RESULT_OK);
            finish();
        });

    }

}
