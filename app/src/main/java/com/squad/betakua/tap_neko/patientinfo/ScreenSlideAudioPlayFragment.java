package com.squad.betakua.tap_neko.patientinfo;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TimePicker;

import com.airbnb.lottie.LottieAnimationView;
import com.squad.betakua.tap_neko.R;
import com.squad.betakua.tap_neko.notifications.AlarmReceiver;
import com.squad.betakua.tap_neko.notifications.NotificationScheduler;

import java.io.IOException;
import java.util.Calendar;

public class ScreenSlideAudioPlayFragment extends Fragment {

    private LottieAnimationView playToPauseAudioIcon;
    private ConstraintLayout fab_layout;
    private boolean isFABOpen;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_audio_panel, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        playToPauseAudioIcon = getView().findViewById(R.id.play_to_pause_animation);

        playToPauseAudioIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playToPauseAudioIcon.playAnimation();
                //Play audio file

                String filePath = Environment.getExternalStorageDirectory()+"/Download/text.mp3";
                try {
                    final MediaPlayer mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(filePath);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mediaPlayer.stop();
                        }
                    }, 10000);
                } catch (IOException e) {
                    return;
                }

            }
        });

        FloatingActionButton mainFab = getView().findViewById(R.id.mainFab);
        FloatingActionButton callFab = getView().findViewById(R.id.callFab);
        FloatingActionButton alertFab = getView().findViewById(R.id.alertFab);

        alertFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog();
            }
        });

        callFab.setVisibility(View.INVISIBLE);
        alertFab.setVisibility(View.INVISIBLE);

        mainFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callFab.getVisibility()==View.VISIBLE){
                    callFab.setVisibility(View.INVISIBLE);
                    alertFab.setVisibility(View.INVISIBLE);
                } else{
                    callFab.setVisibility(View.VISIBLE);
                    alertFab.setVisibility(View.VISIBLE);
                }
            }

        });
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
}
