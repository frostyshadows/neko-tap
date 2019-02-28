package com.squad.betakua.tap_neko.audiorecord;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.microsoft.cognitiveservices.speech.CancellationReason;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import com.squad.betakua.tap_neko.BuildConfig;
import com.squad.betakua.tap_neko.R;
import com.squad.betakua.tap_neko.azure.AzureInterface;
import com.squad.betakua.tap_neko.azure.AzureInterfaceException;

import java.io.File;
import java.io.FileInputStream;

import static com.squad.betakua.tap_neko.PharmacistActivity.AUDIO_REQ_KEY;
import static com.squad.betakua.tap_neko.PharmacistActivity.BARCODE_KEY;

/**
 * Created by sherryuan on 2019-01-26.
 */

public class AudioRecorderActivity extends AppCompatActivity {
    private static final String SPEECH_SUB_KEY = BuildConfig.azure_speech_key1;
    private static final String SERVICE_REGION = "westus";

    private Button recordButton;
    private Button stopButton;
    private Button playButton;
    private Button saveButton;

    private static int REQUEST_CODE = 24;
    private static final int PERMISSION_RECORD_AUDIO = 0;
    private MediaRecorder audioRecorder;
    private File outputFile;

    // Azure Speech Service
    private SpeechConfig config;
    private TextView statusText;
    private TextView outputText;
    private Thread textThread;
    private String recognizedText = "Recognizing Text...";
    private RecordWaveTask recordTask;
    private SpeechRecognizer recognizerWav;
    private String audioFileName;

    // Mock values
    private static final String MOCK_NFC_ID = "23233301";
    private static final String MOCK_PRODUCT_ID = "99965666";
    private static final String MOCK_YOUTUBE_URL = "https://www.youtube.com/watch?v=VYMDHaQMj_w";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recorder);

        try {
            AzureInterface.init(getApplicationContext());
            // AzureInterface.init(AzureSpeechActivity.this); // initialize singleton
        } catch (AzureInterfaceException e) {
            Log.e("ERROR", e.toString());
        }


        // TODO: initialize with drug product code
        // ---------------------- Azure Speech Config ----------------------
        config = SpeechConfig.fromSubscription(SPEECH_SUB_KEY, SERVICE_REGION);
        audioFileName = "azure_test2"; // TODO: replace with nfc code
        String audioFileName2 = "azure_test2.wav"; // TODO: replace with nfc code

        recordTask = (RecordWaveTask) getLastCustomNonConfigurationInstance();
        if (recordTask == null) {
            recordTask = new RecordWaveTask();
        }

        // outputFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), audioFileName2);
        outputFile = new File(getFilesDir(), audioFileName2);
        // String newPath = FilePathURI.getFilePath(outputFile.getPath());
        // Log.e("outputFile is ", outputFile + " " + outputFile.getAbsolutePath());


        //the selected audio.
        // Uri uri = data.getData();
        //
        // String path = getPath(uri);
        // try {
        //     speech(path);
        //     // } catch (ExecutionException e) {
        //     e.printStackTrace();
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // }


        AudioConfig audioInput = AudioConfig.fromWavFileInput(outputFile.getAbsolutePath());
        recognizerWav = new SpeechRecognizer(config, audioInput);

        outputText = this.findViewById(R.id.azure_speech_live_output);
        statusText = this.findViewById(R.id.azure_speech_status);

        // ask for permissions
        if (ContextCompat.checkSelfPermission(AudioRecorderActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AudioRecorderActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            return;
        }
        if (ContextCompat.checkSelfPermission(AudioRecorderActivity.this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AudioRecorderActivity.this,
                    new String[] { Manifest.permission.RECORD_AUDIO },
                    PERMISSION_RECORD_AUDIO);
            return;
        }

        initThread();
        setupAudioWav();
        initAudioRecorder();
        initRecordButton();
        initStopButton();
        initPlayButton();
        initSaveButton();
        stopButton.setEnabled(false);
        playButton.setEnabled(false);
        saveButton.setEnabled(false);
    }

    private void initThread() {
        textThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!textThread.isInterrupted()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                outputText.setText(recognizedText);
                            }
                        });
                    }
                } catch (Exception e) {
                    Log.d("ERROR: ", e.toString());
                }
            }
        };
    }

    private void initAudioRecorder() {
        audioRecorder = new MediaRecorder();
        audioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        audioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        audioRecorder.setOutputFile(outputFile.getAbsolutePath());
    }

    private void initRecordButton() {
        recordButton = findViewById(R.id.record_button);
        recordButton.setOnClickListener((View view) -> {
            recordAudioWav(outputFile);
            Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
        });
    }

    private void initStopButton() {
        stopButton = findViewById(R.id.stop_button);
        stopButton.setOnClickListener((View view) -> {
            if (!recordTask.isCancelled() && recordTask.getStatus() == AsyncTask.Status.RUNNING) {
                recordTask.cancel(false);
            } else {
                Toast.makeText(this, "Task not running.", Toast.LENGTH_SHORT).show();
            }

            if (ContextCompat.checkSelfPermission(AudioRecorderActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(AudioRecorderActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            } else {
                audioRecorder.stop();
                audioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                audioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                audioRecorder.setOutputFile(outputFile.getAbsolutePath());
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
            if (outputFile == null) {
                statusText.setText("Cannot play audio, defaultWavFile is null");
                return;
            }

            Log.e("Playing from ", outputFile.getAbsolutePath());
            if (recordTask.isCancelled() && !(recordTask.getStatus() == AsyncTask.Status.RUNNING)) {
                MediaPlayer mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(outputFile.getAbsolutePath());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    statusText.setText("Playing .wav file from " + outputFile.getAbsolutePath());
                    // Toast.makeText(getApplicationContext(), "Playing Audio", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("ERROR at playAudioWav:", e.toString());
                }
            } else {
                // Toast.makeText(AzureSpeech.this, "Audio is Recording.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initSaveButton() {
        saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener((View view) -> {
            FileInputStream fileInputStream;
            try {
                fileInputStream = new FileInputStream(outputFile);
                // AzureInterface.getInstance().uploadAudio(audioFileName, fileInputStream, outputFile.length());
                // AzureInterface.getInstance().writeInfoItem(MOCK_NFC_ID, MOCK_PRODUCT_ID, recognizedText, MOCK_YOUTUBE_URL);
            } catch (Exception e) {
                Log.e("ERROR", e.toString());
            }
        });
        statusText.setText("wav file saved at: " + outputFile.getAbsolutePath());
    }

    public void recordAudioWav(File wavFile) {
        switch (recordTask.getStatus()) {
            case RUNNING:
                Toast.makeText(AudioRecorderActivity.this, "Task already running...", Toast.LENGTH_SHORT).show();
                return;
            case FINISHED:
                recordTask = new RecordWaveTask();
                break;
            case PENDING:
                if (recordTask.isCancelled()) {
                    recordTask = new RecordWaveTask();
                }
        }
        recordTask.execute(wavFile);
    }

    public void setupAudioWav() {
        AudioConfig audioInput = AudioConfig.fromWavFileInput(outputFile.getAbsolutePath());
        recognizerWav = new SpeechRecognizer(config, audioInput);

        recognizerWav.recognizing.addEventListener((s, e) -> {
            outputText.setText(e.getResult().getText());
            System.out.println("RECOGNIZING: Text=" + e.getResult().getText());
        });

        recognizerWav.recognized.addEventListener((s, e) -> {
            if (e.getResult().getReason() == ResultReason.RecognizedSpeech) {
                recognizedText = e.getResult().getText();
                System.out.println("RECOGNIZED: Text=" + e.getResult().getText());
            }
            else if (e.getResult().getReason() == ResultReason.NoMatch) {
                System.out.println("NOMATCH: Speech could not be recognized.");
            }
        });

        recognizerWav.canceled.addEventListener((s, e) -> {
            System.out.println("CANCELED: Reason=" + e.getReason());

            if (e.getReason() == CancellationReason.Error) {
                System.out.println("CANCELED: ErrorCode=" + e.getErrorCode());
                System.out.println("CANCELED: ErrorDetails=" + e.getErrorDetails());
                System.out.println("CANCELED: Did you update the subscription info?");
            }
        });

        recognizerWav.sessionStarted.addEventListener((s, e) -> {
            System.out.println("\n    Session started event.");
        });

        recognizerWav.sessionStopped.addEventListener((s, e) -> {
            try {
                recognizerWav.stopContinuousRecognitionAsync().get();
                textThread.interrupt();
            } catch (Exception ex) {
                Log.e("ERROR processAudioWav", ex.toString());
                return;
            }
            System.out.println("\n    Session stopped event.");
        });
    }

}
