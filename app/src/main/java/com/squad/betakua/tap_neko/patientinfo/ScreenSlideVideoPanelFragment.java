package com.squad.betakua.tap_neko.patientinfo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.VideoView;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;

import android.widget.Button;
import android.widget.TimePicker;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerView;
import com.squad.betakua.tap_neko.R;
import com.squad.betakua.tap_neko.notifications.AlarmReceiver;
import com.squad.betakua.tap_neko.notifications.NotificationScheduler;

import java.util.Calendar;


public class ScreenSlideVideoPanelFragment extends Fragment {


    //API key for the Google API
    public static final String API_KEY = "AIzaSyCK1ylJ9-3lMjHU8RuRWsanA1bbhZ5n42Q";

    YouTubePlayerView youTubePlayerView;
    YouTubePlayer.OnInitializedListener onInitializedListener;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_video_panel, container, false);


        return rootView;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        FloatingActionButton mainFab = getView().findViewById(R.id.mainFab);
        FloatingActionButton callFab = getView().findViewById(R.id.callFab);
        FloatingActionButton alertFab = getView().findViewById(R.id.alertFab);

        Button playButton = getView().findViewById(R.id.open_yt_button);
        playButton.setText("Play");

        youTubePlayerView = (YouTubePlayerView) view.findViewById(R.id.youtube_player_view);
        onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo("a1sn_UlUOio");
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                //TODO
            }
        };

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                youTubePlayerView.initialize(API_KEY,onInitializedListener);
            }
        });


        //VideoPlayer
        // VideoView videoView = getView().findViewById(R.id.videoView);
        //
        // if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
        //     ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.INTERNET}, REQUEST_CODE);
        // } else {
        //     Uri uri = Uri.parse("https://www.youtube.com/watch?v=a1sn_UlUOio");
        //     videoView.setVideoURI(uri);
        //     videoView.start();
        //     videoView.setOnClickListener(new View.OnClickListener() {
        //
        //         @Override
        //         public void onClick(View view) {
        //             DisplayMetrics metrics = new DisplayMetrics();
        //             getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        //             android.widget.LinearLayout.LayoutParams params = (android.widget.LinearLayout.LayoutParams) videoView.getLayoutParams();
        //             params.width = metrics.widthPixels;
        //             params.height = metrics.heightPixels;
        //             params.leftMargin = 0;
        //             videoView.setLayoutParams(params);
        //         }
        //
        //     });
        // }

        alertFab.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            showTimePickerDialog();
                                        }
                                    });



        callFab.setVisibility(View.INVISIBLE);
        alertFab.setVisibility(View.INVISIBLE);

        mainFab.setOnClickListener((View v) -> {
            if (callFab.getVisibility() == View.VISIBLE) {
                callFab.setVisibility(View.INVISIBLE);
                alertFab.setVisibility(View.INVISIBLE);
            } else {
                callFab.setVisibility(View.VISIBLE);
                alertFab.setVisibility(View.VISIBLE);
            }
        });

//        playButton.setOnClickListener((View v) -> {
//            Intent browserIntent =
//                    new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=a1sn_UlUOio"));
//            startActivity(browserIntent);
//        });
    }

    private void showTimePickerDialog() {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                NotificationScheduler.setReminder(getContext(), AlarmReceiver.class,
                        selectedHour, selectedMinute, "title", "dosage");
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }



}
