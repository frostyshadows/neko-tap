package com.squad.betakua.tap_neko.patientinfo;

import android.content.Context;
import android.net.Uri;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.TextView;

import android.widget.Button;
import android.widget.TimePicker;

import com.squad.betakua.tap_neko.R;
import com.squad.betakua.tap_neko.notifications.AlarmReceiver;
import com.squad.betakua.tap_neko.notifications.NotificationScheduler;

import java.util.Calendar;


public class ScreenSlideVideoPanelFragment extends Fragment {

    private TextView videoTitle;

    // Navigation Bar
    private ImageButton navButtonLeft;
    private ImageButton navButtonRight;
    private OnButtonClickListener navButtonListener;

    private String productName;
    private String MOCK_PRODUCT_NAME = "Doxycycline 100mg Tablets";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_video_panel, container, false);

        navButtonLeft = rootView.findViewById(R.id.patient_view_text_button);
        navButtonRight = rootView.findViewById(R.id.patient_tap_again_icon);
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
        videoTitle = view.findViewById(R.id.patient_video_title);
        productName = MOCK_PRODUCT_NAME + "\n" + getArguments().getString("productName", "");
        videoTitle.setText(productName);

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

        Button ytButton = getView().findViewById(R.id.open_yt_button);
        ytButton.setText("Open in YouTube");


        ytButton.setOnClickListener((View v) -> {
            Intent browserIntent =
                    new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=a1sn_UlUOio"));
            startActivity(browserIntent);
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
                NotificationScheduler.setReminder(getContext(), AlarmReceiver.class,
                        selectedHour, selectedMinute, "title", "dosage");
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        navButtonListener = (OnButtonClickListener) context;
    }
}
