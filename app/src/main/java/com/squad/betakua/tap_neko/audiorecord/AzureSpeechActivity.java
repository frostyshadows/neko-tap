package com.squad.betakua.tap_neko.audiorecord;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.microsoft.cognitiveservices.speech.CancellationReason;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechRecognizer;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import com.squad.betakua.tap_neko.BuildConfig;
import com.squad.betakua.tap_neko.Constants;
import com.squad.betakua.tap_neko.R;
import com.squad.betakua.tap_neko.azure.AzureInterface;
import com.squad.betakua.tap_neko.azure.AzureInterfaceException;
import com.squad.betakua.tap_neko.azure.OnUploadAudioFileListener;
import com.squad.betakua.tap_neko.utils.Utils;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static com.squad.betakua.tap_neko.PharmacistActivity.AUDIO_TRANSCRIPT_KEY;
import static com.squad.betakua.tap_neko.PharmacistActivity.AUDIO_TRANSLATE_KEY;

public class AzureSpeechActivity extends AppCompatActivity {
    private static final int PERMISSION_RECORD_AUDIO = 0;
    private String TRANSLATE_KEY;

    private TextView outputText;
    private TextView statusText;
    private Thread textThread;
    private String recognizedText = "";
    private String translation = "";
    private EditText translationsText;

    private Button startButton;
    private Button stopButton;
    private Button playButton;
    private Button processButton;

    // Audio Save to Wav
    private File outputFile;
    private RecordWaveTask recordTask;
    private SpeechRecognizer recognizerWav;
    private String nfcId;
    private String fileId;
    private String audioFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_azure_speech);

        // ---------------------- Permissions ----------------------
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    PERMISSION_RECORD_AUDIO);
            // return;
        }

        // Translate
        TRANSLATE_KEY = BuildConfig.nekotap_translate_1;
        translationsText = findViewById(R.id.azure_speech_translated_output);
        translationsText.setFocusable(false);

        statusText = this.findViewById(R.id.azure_speech_status);
        startButton = this.findViewById(R.id.speech_button_wav_start);
        stopButton = this.findViewById(R.id.speech_button_wav_stop);
        playButton = this.findViewById(R.id.speech_button_wav_play);
        processButton = this.findViewById(R.id.speech_button_wav_process);
        setButtonVisibilities(true, false, false, false);

        initTextOutputThread();
        initAudioRecorder();
        initSpeechRecognizer();
    }

    private void initTextOutputThread() {
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
    }

    private void initAudioRecorder() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            nfcId = extras.getString("nfcId", Constants.DEMO_NFC_CODE.toString());
            fileId = Utils.nfcToFileName(nfcId);
            audioFileName = Utils.nfcToFileName(nfcId) + ".wav";
        }

        outputFile = new File(getFilesDir() + audioFileName);

        // Create file if doesn't exist
        try {
            if (!outputFile.exists()) {
                boolean result = outputFile.createNewFile();
                Log.e("LOG RESULT", " " + result);
            }
        } catch (IOException e) {
            Log.e("ERROR ", e.toString());
        }

        // Initiate a new recordTask object
        recordTask = (RecordWaveTask) getLastCustomNonConfigurationInstance();
        if (recordTask == null) {
            recordTask = new RecordWaveTask();
        }
    }

    private void setButtonVisibilities(boolean start, boolean stop, boolean play, boolean process) {
        startButton.setEnabled(start);
        stopButton.setEnabled(stop);
        playButton.setEnabled(play);
        processButton.setEnabled(process);
    }

    public void startAudioWav(View v) {
        // reset output text field
        stopTextThreadIfAlive();
        outputText.setText("");

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

        recordTask.execute(outputFile);
        setButtonVisibilities(false, true, false, false);
    }

    public void stopAudioWav(View v) {
        // reset output text field
        stopTextThreadIfAlive();
        outputText.setText("");

        if (!recordTask.isCancelled() && recordTask.getStatus() == AsyncTask.Status.RUNNING) {
            recordTask.cancel(false);
        } else {
            Toast.makeText(AzureSpeechActivity.this, "Task not running.", Toast.LENGTH_SHORT).show();
        }
        setButtonVisibilities(true, false, true, false);
    }

    public void playAudioWav(View v) {
        // reset output text field
        stopTextThreadIfAlive();
        outputText.setText("");

        setButtonVisibilities(true, false, false, false);
        if (outputFile == null) {
            statusText.setText("Cannot play audio, outputFile is null");
            return;
        }

        // display text on screen
        processAudioWav();

        String outputFilePath = outputFile.getAbsolutePath();
        if (recordTask.isCancelled() && !(recordTask.getStatus() == AsyncTask.Status.RUNNING)) {
            MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                Toast.makeText(AzureSpeechActivity.this, "Playing Audio", Toast.LENGTH_SHORT).show();
                mediaPlayer.setDataSource(outputFilePath);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ERROR at playAudioWav:", e.toString());
            }
        } else {
            Toast.makeText(AzureSpeechActivity.this, "Busy", Toast.LENGTH_SHORT).show();
        }
        setButtonVisibilities(true, false, true, true);

    }

    public void initSpeechRecognizer() {
        AudioConfig audioConfig = AudioConfig.fromWavFileInput(outputFile.getAbsolutePath());
        try {
            recognizerWav = AzureInterface.getInstance().getSpeechRecognizer(audioConfig);
        } catch (AzureInterfaceException e) {
            Log.e("ERROR: in getSpeechRecognizer() ", e.toString());
            return;
        }

        recognizerWav.recognizing.addEventListener((s, e) -> {
            outputText.setText(e.getResult().getText());
            System.out.println("RECOGNIZING: Text=" + e.getResult().getText());
        });

        recognizerWav.recognized.addEventListener((s, e) -> {
            if (e.getResult().getReason() == ResultReason.RecognizedSpeech) {
                recognizedText = recognizedText + e.getResult().getText();
                System.out.println("RECOGNIZED: Text=" + e.getResult().getText());
            } else if (e.getResult().getReason() == ResultReason.NoMatch) {
                System.out.println("NOMATCH: Speech could not be recognized.");
            }
        });

        recognizerWav.canceled.addEventListener((s, e) -> {
            System.out.println("CANCELED: Reason=" + e.getReason());
            new LongOperation().execute(recognizedText);

            if (e.getReason() == CancellationReason.Error) {
                System.out.println("CANCELED: ErrorCode=" + e.getErrorCode());
                System.out.println("CANCELED: ErrorDetails=" + e.getErrorDetails());
                System.out.println("CANCELED: Did you update the subscription info?");
                setButtonVisibilities(true, false, false, false);
                Toast.makeText(AzureSpeechActivity.this, "There was an error with speech recognition", Toast.LENGTH_SHORT).show();
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
                setButtonVisibilities(true, false, false, false);
                Toast.makeText(AzureSpeechActivity.this, "There was an error with speech recognition", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processAudioWav() {
        // Responsible for processing wav file through Azure Speech-to-Text
        textThread.start();

        try {
            recognizerWav.startContinuousRecognitionAsync().get();
        } catch (Exception e) {
            Log.e("ERROR processAudioWav", e.toString());
        }
    }

    public void uploadData(View v) {
        if (fileId == null) {
            Toast.makeText(this, "Please tap an NFC tag first", Toast.LENGTH_SHORT).show();
            return;
        }

        stopTextThreadIfAlive();

        FileInputStream fileInputStream;
        processButton.setText("SUBMITTING...");
        setButtonVisibilities(false, false, false, false);
        try {
            fileInputStream = new FileInputStream(outputFile);
            AzureInterface.getInstance().uploadAudio(fileId, fileInputStream, outputFile.length(), new OnUploadAudioFileListener() {
                @Override
                public void onUploadComplete(String response) {
                    try {
                        // AzureInterface.getInstance().writeInfoItem(MOCK_NFC_ID, MOCK_PRODUCT_ID, recognizedText, MOCK_YOUTUBE_URL);
                        onFinishUpload();
                    } catch (Exception e) {
                        setButtonVisibilities(true, false, false, false);
                    }

                }
            });
        } catch (Exception e) {
            processButton.setEnabled(true);
            setButtonVisibilities(true, false, false, false);
            Toast.makeText(this, "There was an error uploading the file...", Toast.LENGTH_SHORT).show();
        }
    }

    public void onFinishUpload() {
        Intent data = new Intent();
        translation = translationsText.getText().toString();

        data.putExtra(AUDIO_TRANSCRIPT_KEY, recognizedText);
        data.putExtra(AUDIO_TRANSLATE_KEY, translation);

        setResult(RESULT_OK, data);
        finish();
    }

    private void stopTextThreadIfAlive() {
        if (textThread.isAlive()) {
            textThread.interrupt();
        }
    }

    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                String response = Post(params[0]);
                return prettify(response);
            } catch (Exception e) {
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            translationsText.setText(result);
            translationsText.setFocusable(true);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    // This function performs a POST request.
    public String Post(String text) throws IOException {
        String url = "https://api.cognitive.microsofttranslator.com/translate?api-version=3.0&to=ko,it";
        // String url = "https://api.cognitive.microsoft.com/sts/v1.0/" + TRANSLATE_KEY;

        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType,
                "[{\n\t\"Text\": \"" + text + "\"\n}]");
        Request request = new Request.Builder()
                .url(url).post(body)
                .addHeader("Ocp-Apim-Subscription-Key", TRANSLATE_KEY)
                .addHeader("Content-type", "application/json").build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    // This function prettifies the json response.
    public String prettify(String json_text) {
        JsonParser parser = new JsonParser();
        JsonElement json = parser.parse(json_text);
        JsonArray js = json.getAsJsonArray();

        translation = js.get(0).getAsJsonObject().get("translations").getAsJsonArray().get(0).getAsJsonObject().get("text").toString().replace("\"", "");
        return translation;
    }
}
