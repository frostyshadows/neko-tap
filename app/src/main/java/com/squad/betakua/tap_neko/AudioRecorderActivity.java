package com.squad.betakua.tap_neko;

import android.Manifest;
import android.content.Intent;
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

import com.microsoft.cognitiveservices.speech.SpeechRecognizer;
import com.microsoft.cognitiveservices.speech.translation.TranslationRecognizer;
import com.squad.betakua.tap_neko.azure.AzureInterface;
import com.squad.betakua.tap_neko.azure.AzureInterfaceException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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
    private String translatedOutputFile;

    private SpeechRecognizer speechRecognizer;
    private TranslationRecognizer translationRecognizer;

    private String transcription;
    private ArrayList<String> translations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recorder);

        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";
        translatedOutputFile = Environment.getExternalStorageDirectory().getAbsolutePath() +
                "/recording_translated.wav";

        // TODO
        final List<String> outputLanguages = new ArrayList<>();
        outputLanguages.add("fr");

        translations = new ArrayList<>();

        try {
            speechRecognizer = AzureInterface.getInstance().getSpeechRecognizer();
            translationRecognizer = AzureInterface.getInstance().getTranslationRecognizer(outputLanguages);
        } catch (AzureInterfaceException e) {
            e.printStackTrace();
        }

        speechRecognizer.recognized.addEventListener((s, e) -> {
            transcription = e.getResult().getText();
        });

        translationRecognizer.recognized.addEventListener((s, e) -> {
            for (String key : e.getResult().getTranslations().keySet()) {
                translations.add(e.getResult().getTranslations().get(key));
            }
        });

        translationRecognizer.synthesizing.addEventListener((s, e) -> {
            byte[] audioData = e.getResult().getAudio();

            // Play the TTS data of we got more than the wav header.
            if (audioData != null && audioData.length > 44) {
                ByteArrayInputStream is = new ByteArrayInputStream(audioData);
                File file = new File(translatedOutputFile);
                try {
                    OutputStream os = new FileOutputStream(file);
                    byte[] buffer = new byte[is.available()];
                    is.read(buffer);
                    os.write(buffer);
                    is.close();
                    os.close();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        });


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
                speechRecognizer.startContinuousRecognitionAsync();
                translationRecognizer.startContinuousRecognitionAsync();
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
                Future transcriptionEnd = speechRecognizer.stopContinuousRecognitionAsync();
                Future translationEnd = translationRecognizer.stopContinuousRecognitionAsync();
                audioRecorder.stop();
                audioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                audioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                audioRecorder.setOutputFile(outputFile);
                try {
                    transcriptionEnd.get();
                    translationEnd.get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
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
            Intent data = new Intent();
            data.putExtra(TRANSCRIPTION_STR_KEY, transcription);
            data.putStringArrayListExtra(TRANSLATION_STR_KEY, translations);
            setResult(RESULT_OK, data);
            finish();
        });

    }

}
