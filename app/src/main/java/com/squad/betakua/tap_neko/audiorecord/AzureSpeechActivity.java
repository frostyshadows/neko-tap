package com.squad.betakua.tap_neko.audiorecord;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.cognitiveservices.speech.CancellationReason;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import com.microsoft.windowsazure.mobileservices.BuildConfig;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.squad.betakua.tap_neko.R;
import com.squad.betakua.tap_neko.azure.AzureInterface;
import com.squad.betakua.tap_neko.azure.AzureInterfaceException;
import com.squad.betakua.tap_neko.azure_speech.RecordWaveTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

public class AzureSpeechActivity extends AppCompatActivity {
    String apiKey;
    String serviceRegion = "westus";
    SpeechConfig config;
    SpeechRecognizer recognizer;
    TextView outputText;
    TextView statusText;
    Thread textThread;
    StringBuilder speechResult;
    String recognizedText = "Output Text";
    Socket socket;
    String response = "";
    private static final String SPEECH_SUB_KEY = com.squad.betakua.tap_neko.BuildConfig.azure_speech_key1;

    private MobileServiceClient mClient;
    private static String url = "tapthecat.azurewebsites.net";
    // private static Integer port = 443;

    // Audio Streaming
    public byte[] buffer;
    public static DatagramSocket audioStreamSocket;
    private int port = 443;
    private AudioRecord recorder;
    private int sampleRate = 16000 ; // 44100 for music
    private int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    int minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
    private boolean status = false;

    // Audio Save to Wav
    private static String defaultWavFileName;
    private File defaultWavFile;
    private static final int PERMISSION_RECORD_AUDIO = 0;
    private RecordWaveTask recordTask = null;
    private SpeechRecognizer recognizerWav;

    // AzureInterface
    // AzureInterface azureInterface;
    private static final String MOCK_NFC_ID = "23233301";
    private static final String MOCK_PRODUCT_ID = "99965666";
    private static final String MOCK_YOUTUBE_URL = "https://www.youtube.com/watch?v=VYMDHaQMj_w";
    private static final String MOCK_AUDIO_TITLE = "azure_audio_test_1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_azure_speech);

        // ---------------------- Permissions ----------------------
        if (ContextCompat.checkSelfPermission(AzureSpeechActivity.this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AzureSpeechActivity.this,
                    new String[] { Manifest.permission.RECORD_AUDIO },
                    PERMISSION_RECORD_AUDIO);
            return;
        }

        // ---------------------- Status Text ----------------------
        statusText = this.findViewById(R.id.azure_speech_status);

        // ---------------------- Output Text Thread ----------------------
        speechResult = new StringBuilder();
        outputText = this.findViewById(R.id.azure_speech_text_output);
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
                    Log.d("textThread Interrupted", "---");
                }
            }
        };

        // ---------------------- Azure Interface ----------------------

        try {
            AzureInterface.init(getApplicationContext());
        } catch (AzureInterfaceException e) {
            Log.e("ERROR", e.toString());
        }

        // ---------------------- Azure Speech Config ----------------------
        apiKey = SPEECH_SUB_KEY;
        config = SpeechConfig.fromSubscription(apiKey, serviceRegion);

        // ---------------------- Azure Speech ----------------------
        try {
            mClient = new MobileServiceClient(
                    "https://tapthecat.azurewebsites.net",
                    this
            );
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Error onCreate ", e.toString());
        }

        // ---------------------- Audio Save to Wav ----------------------
        // Set default wav file
        defaultWavFileName = getFilesDir() + "/azure_test.wav";
        defaultWavFile = new File(defaultWavFileName);
        // defaultWavFile = new File(getFilesDir(), defaultWavFileName);

        // File file = new File(defaultWavFileName);


        try {
            if(!defaultWavFile.exists()) {
                boolean result = defaultWavFile.createNewFile();
                Log.e("LOG RESULT", " " + result);
                FileOutputStream fos = openFileOutput("azure_test.wav", MODE_PRIVATE);
                fos.write(0);
                fos.close();
                // write code for saving data to the file
            }
        } catch (IOException e) {
            Log.e("ERROR ", e.toString());
        }

        // defaultWavFile = new File(getFilesDir(), defaultWavFileName);

        // Initiate a new recordTask object
        recordTask = (RecordWaveTask) getLastCustomNonConfigurationInstance();
        if (recordTask == null) {
            recordTask = new RecordWaveTask();
        }

        // Setup recognizer event listeners
        setupAudioWav();

        // ---------------------- Sockets ----------------------
        setupSocket();

        // ---------------------- Azure Speech ----------------------
        // Setup Speech Recognizer Event Listeners
        recognizer = new SpeechRecognizer(config);

        recognizer.recognizing.addEventListener((s, e) -> {
            recognizedText = e.getResult().getText();
            Log.d("TEXT RECOGNITION: ", "RECOGNIZING: Text=" + e.getResult().getText());
        });
    }

    private void setupSocket() {
        try {
            socket = new Socket(url, port);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
            byte[] buffer = new byte[1024];

            int bytesRead;
            InputStream inputStream = socket.getInputStream();
            Log.e("testing ", "awefawefawef");
            while ((bytesRead = inputStream.read(buffer)) != 1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
                response += byteArrayOutputStream.toString("UTF-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Error setupSocket ", e.toString());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static String readStream(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            Log.e("ERROR", "IOException", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e("ERROR", "IOException", e);
            }
        }
        return sb.toString();
    }

    private static class AsyncRequest extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            HttpURLConnection client = null;

            try {
                URL url = new URL("https://tapthecat.azurewebsites.net/");
                client = (HttpURLConnection) url.openConnection();
                client.setRequestMethod("GET");

                // InputStream inputStream = client.getInputStream();
                Log.e("HTTP Request: ", "test2");
                InputStream bufferedReader = new BufferedInputStream(client.getInputStream());
                // InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                String result = readStream(bufferedReader);
                Log.e("HTTP Request: ", "test3");

                Log.e("HTTP Result: ", result);
                Log.e("HTTP Request: ", "test4");
                // String line = bufferedReader.readLine();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("Error ", e.toString());
            } finally {
                if(client != null) // Make sure the connection is not null.
                    client.disconnect();
            }

            return "";
        }

        // @Override
        // protected void onPostExecute(String result) {
        //     textView.setText(result);
        // }
    }

    public void startAudioWav(View v) {
        switch (recordTask.getStatus()) {
            case RUNNING:
                Toast.makeText(this, "Task already running...", Toast.LENGTH_SHORT).show();
                return;
            case FINISHED:
                recordTask = new RecordWaveTask();
                break;
            case PENDING:
                if (recordTask.isCancelled()) {
                    recordTask = new RecordWaveTask();
                }
        }
        Toast.makeText(this, defaultWavFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
        statusText.setText("wav file saved at: " + defaultWavFile.getAbsolutePath());
        recordTask.execute(defaultWavFile);
    }

    public void stopAudioWav(View v) {
        if (!recordTask.isCancelled() && recordTask.getStatus() == AsyncTask.Status.RUNNING) {
            recordTask.cancel(false);
        } else {
            Toast.makeText(AzureSpeechActivity.this, "Task not running.", Toast.LENGTH_SHORT).show();
        }
    }

    public void playAudioWav(View v) {
        if (defaultWavFile == null) {
            statusText.setText("Cannot play audio, defaultWavFile is null");
            return;
        }

        // String outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";
        String outputFile = defaultWavFile.getAbsolutePath();

        Log.e("Playing from ", outputFile);
        if (recordTask.isCancelled() && !(recordTask.getStatus() == AsyncTask.Status.RUNNING)) {
            MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(outputFile);
                mediaPlayer.prepare();
                mediaPlayer.start();
                statusText.setText("Playing .wav file from " + outputFile);
                Toast.makeText(getApplicationContext(), "Playing Audio", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ERROR at playAudioWav:", e.toString());
            }
        } else {
            Toast.makeText(AzureSpeechActivity.this, "Audio is Recording.", Toast.LENGTH_SHORT).show();
        }
    }

    public void setupAudioWav() {
        AudioConfig audioInput = AudioConfig.fromWavFileInput(defaultWavFile.getAbsolutePath());
        Log.e("audioInput: ", audioInput.toString());
        recognizerWav = new SpeechRecognizer(config, audioInput);

        recognizerWav.recognizing.addEventListener((s, e) -> {
            outputText.setText(e.getResult().getText());
            System.out.println("RECOGNIZING: Text=" + e.getResult().getText());
        });

        recognizerWav.recognized.addEventListener((s, e) -> {
            if (e.getResult().getReason() == ResultReason.RecognizedSpeech) {
                recognizedText = e.getResult().getText();
                System.out.println("RECOGNIZED: Text=" + e.getResult().getText());
                // try {
                //     recognizerWav.stopContinuousRecognitionAsync().get();
                // } catch (Exception ex) {
                //     Log.e("ERROR processAudioWav", ex.toString());
                // }
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
            } finally {
                uploadData();
            }
            System.out.println("\n    Session stopped event.");
        });
    }

    public void processAudioWav(View v) {
        // Responsible for processing wav file through Azure Speech-to-Text
        textThread.start();

        try {
            recognizerWav.startContinuousRecognitionAsync().get();
        } catch (Exception e) {
            Log.e("ERROR processAudioWav", e.toString());
        }
    }

    public void uploadData() {
        FileInputStream fileInputStream;

        try {
            fileInputStream = new FileInputStream(defaultWavFile);
            AzureInterface.getInstance().uploadAudio(MOCK_AUDIO_TITLE, fileInputStream, defaultWavFile.length());
            AzureInterface.getInstance().writeInfoItem(MOCK_NFC_ID, MOCK_PRODUCT_ID, recognizedText, MOCK_YOUTUBE_URL);
        } catch (Exception e) {
            Log.e("ERROR", e.toString());
        }
    }

    public void downloadAudio(View v) {
        String outputFileName = "azure_audio_download_1.wav";
        File outputFile = new File(getFilesDir(), outputFileName);

        try {
            if (!outputFile.exists()) {
                outputFile.createNewFile();
            }
        } catch (Exception e) {
            Log.e("ERROR", e.toString());
        }

        new Thread(() -> {
            try {
                FileOutputStream fileOutputStream;
                fileOutputStream = new FileOutputStream(outputFile);
                AzureInterface.getInstance().downloadAudio(MOCK_AUDIO_TITLE, fileOutputStream);
            } catch (Exception e) {
                Log.e("ERROR", e.toString());
            }

            MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(outputFile.getAbsolutePath());
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ERROR at playAudioWav:", e.toString());
            }
        }).start();
    }
}
