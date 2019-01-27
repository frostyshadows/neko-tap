package com.squad.betakua.tap_neko;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import com.squad.betakua.tap_neko.notifications.AlarmReceiver;
import com.squad.betakua.tap_neko.notifications.NotificationScheduler;

import java.util.Calendar;

public class ScreenSlideVideoPanelFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_video_panel, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        FloatingActionButton mainFab = getView().findViewById(R.id.mainFab);
        FloatingActionButton callFab = getView().findViewById(R.id.callFab);
        FloatingActionButton alertFab = getView().findViewById(R.id.alertFab);

        Button ytButton = getView().findViewById(R.id.open_yt_button);

        alertFab.setOnClickListener((View v) -> {
            showTimePickerDialog();
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


}
