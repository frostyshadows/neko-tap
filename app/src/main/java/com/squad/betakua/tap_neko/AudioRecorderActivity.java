package com.squad.betakua.tap_neko;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
import cafe.adriel.androidaudioconverter.model.AudioFormat;

/**
 * Created by sherryuan on 2019-01-26.
 */

public class AudioRecorderActivity extends AppCompatActivity {

    private Button recordButton;
    private Button stopButton;
    private Button playButton;

    private MediaRecorder audioRecorder;
    private String outputFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recorder);

        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";
        initAudioRecorder();

        initRecordButton();
        initStopButton();
        initPlayButton();
        stopButton.setEnabled(false);
        playButton.setEnabled(false);

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
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    audioRecorder.prepare();
                    audioRecorder.start();
                } catch (IllegalStateException ise) {
                    // make something ...
                } catch (IOException ioe) {
                    // make something
                    ioe.printStackTrace();
                }
                recordButton.setEnabled(false);
                stopButton.setEnabled(true);
                Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initStopButton() {
        stopButton = findViewById(R.id.stop_button);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audioRecorder.stop();
                audioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                audioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                audioRecorder.setOutputFile(outputFile);

                //Convert audio to .wav file
                convert();

                recordButton.setEnabled(true);
                stopButton.setEnabled(false);
                playButton.setEnabled(true);
                Toast.makeText(getApplicationContext(), "Audio Recorder stopped", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initPlayButton() {
        playButton = findViewById(R.id.play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });
    }

    private void convert(){
        File flacFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp");
        IConvertCallback callback = new IConvertCallback() {
            @Override
            public void onSuccess(File convertedFile) {
                // So fast? Love it!
            }
            @Override
            public void onFailure(Exception error) {
                // Oops! Something went wrong
            }
        };
        AndroidAudioConverter.with(this)
                // Your current audio file
                .setFile(flacFile)

                // Your desired audio format
                .setFormat(AudioFormat.MP3)

                // An callback to know when conversion is finished
                .setCallback(callback)

                // Start conversion
                .convert();
    }
}
