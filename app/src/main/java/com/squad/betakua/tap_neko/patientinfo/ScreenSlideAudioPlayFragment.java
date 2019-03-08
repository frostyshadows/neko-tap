package com.squad.betakua.tap_neko.patientinfo;

import android.app.TimePickerDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.squad.betakua.tap_neko.R;
import com.squad.betakua.tap_neko.azure.AzureInterface;
import com.squad.betakua.tap_neko.azure.OnDownloadAudioFileListener;
import com.squad.betakua.tap_neko.notifications.AlarmReceiver;
import com.squad.betakua.tap_neko.notifications.NotificationScheduler;
import com.squad.betakua.tap_neko.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class ScreenSlideAudioPlayFragment extends Fragment {
    // Header
    private TextView title;
    private String productName;

    // Navigation Bar
    private ImageButton navButtonLeft;
    private ImageButton navButtonRight;
    private OnButtonClickListener navButtonListener;

    // Audio
    private LottieAnimationView lottiePlayToPause;
    private LottieAnimationView lottiePauseToPlay;
    private String audioFilePath;

    private String nfcId;
    private String fileId;
    private File audioFile;
    private MediaPlayer mediaPlayer;
    private boolean audioIsReady;
    private String MOCK_PRODUCT_NAME = "Doxycycline 100mg Tablets";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_audio_panel, container, false);

        audioFilePath = getArguments().getString("audioFilePath", "");
        productName = MOCK_PRODUCT_NAME + "\n" + getArguments().getString("productName", "");
        nfcId = getArguments().getString("nfcId", "");
        fileId = Utils.nfcToFileName(nfcId);
        mediaPlayer = new MediaPlayer();

        navButtonLeft = rootView.findViewById(R.id.patient_tap_again_icon);
        navButtonRight = rootView.findViewById(R.id.patient_view_text_button);
        navButtonLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navButtonListener.onButtonClicked(v);
            }
        });
        navButtonRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navButtonListener.onButtonClicked(v);
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        title = view.findViewById(R.id.patient_audio_title);
        title.setText(productName);

        lottiePlayToPause = view.findViewById(R.id.play_to_pause_animation);
        lottiePauseToPlay = view.findViewById(R.id.pause_to_play_animation);

        lottiePlayToPause.setOnClickListener(this::onAudioClick);
        lottiePauseToPlay.setOnClickListener(this::onAudioClick);
        lottiePauseToPlay.setVisibility(View.INVISIBLE);
        lottiePauseToPlay.setEnabled(false);

        mediaPlayer = new MediaPlayer();
        loadData();
    }

    public void onAudioClick(View v) {
        if (!audioIsReady) {
            Toast.makeText(getActivity().getApplicationContext(), "Audio file still downloading. Please wait a moment.", Toast.LENGTH_LONG).show();
            return;
        }

        if (audioFilePath.equals("") | audioFilePath == null) {
            Toast.makeText(getActivity().getApplicationContext(), "Audio file unavailable", Toast.LENGTH_LONG).show();
            return;
        }

        if (!mediaPlayer.isPlaying()) {
            lottiePauseToPlay.setVisibility(View.INVISIBLE);
            lottiePauseToPlay.setEnabled(false);

            lottiePlayToPause.setVisibility(View.VISIBLE);
            lottiePlayToPause.setEnabled(true);
            lottiePlayToPause.setProgress(0.0f);
            lottiePlayToPause.playAnimation();

            mediaPlayer.start();
        } else if (mediaPlayer.isPlaying()) {
            lottiePlayToPause.setVisibility(View.INVISIBLE);
            lottiePlayToPause.setEnabled(false);

            lottiePauseToPlay.setVisibility(View.VISIBLE);
            lottiePauseToPlay.setEnabled(true);
            lottiePauseToPlay.setProgress(0.0f);
            lottiePauseToPlay.playAnimation();

            mediaPlayer.pause();
        }
    }

    private void loadData() {
        try {
            audioFile = new File(audioFilePath);
            createFileIfNotExists(audioFile);

            new Thread(() -> {
                try {
                    FileOutputStream fileOutputStream;
                    fileOutputStream = new FileOutputStream(audioFile);
                    AzureInterface.getInstance().downloadAudio(fileId, fileOutputStream, new OnDownloadAudioFileListener() {
                        @Override
                        public void onDownloadComplete(String response) {
                            try {
                                initMediaPlayer();
                                fileOutputStream.close();
                                audioIsReady = true;
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("ERROR in downloadAudio:", e.toString());
                            }
                        }
                    });
                } catch (Exception e) {
                    Log.e("ERROR", e.toString());
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR", e.toString());
        }
    }

    public void createFileIfNotExists(File file) {
        try {
            if (!file.exists()) {
                boolean success = file.createNewFile();
                Log.e("File download: ", "result is " + success);
            }
        } catch (Exception e) {
            Log.e("ERROR", e.toString());
        }
    }

    public void initMediaPlayer() {
        try {
            mediaPlayer.setDataSource(audioFile.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    lottiePlayToPause.setVisibility(View.INVISIBLE);
                    lottiePlayToPause.setEnabled(false);

                    lottiePauseToPlay.setVisibility(View.VISIBLE);
                    lottiePauseToPlay.setEnabled(true);
                    lottiePauseToPlay.setProgress(0.0f);
                    lottiePauseToPlay.playAnimation();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showTimePickerDialog() {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                NotificationScheduler.setReminder(getContext(),AlarmReceiver.class,
                        selectedHour, selectedMinute, "Doxycycline", "1 tablet");
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    // Enabling buttons to switch between fragments
    // https://stackoverflow.com/questions/23631975/viewpager-how-to-navigate-from-one-page-to-another-using-a-button
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        navButtonListener = (OnButtonClickListener) context;
    }
}
