package com.squad.betakua.tap_neko.patientinfo;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
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

    private String webUrl;
    private String url;
    private String pharmacyPhone;
    private String pharmacyName;
    private String pharmacist;
    private String productName;
    private String MOCK_PRODUCT_NAME = "Doxycycline 100mg Tablets";
    private String MOCK_YOUTUBE_CODE = "ma_cmlU9DxU";

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
        productName = getArguments().getString("productName", "Aerochamber (Child)") + "\nProduct ID: " + getArguments().getString("productID", "80092323");
        url = getArguments().getString("url", MOCK_YOUTUBE_CODE);
        webUrl = getArguments().getString("webUrl", "https://www.aerochambervhc.com/instructions-for-use/");
        pharmacyPhone = getArguments().getString("pharmacyPhone", "1-800-867-1389");
        pharmacyName = getArguments().getString("pharmacyName", "Shoppers Drug Mart #2323");
        pharmacist = getArguments().getString("pharmacist", "John Lee");

        videoTitle.setText(productName);

        // Youtube
        Button ytButton = getView().findViewById(R.id.open_yt_button);

        String youtubeURI = "https://www.youtube.com/watch?v=" + url;
        ytButton.setOnClickListener((View v) -> {
            Intent browserIntent =
                    new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeURI));
            startActivity(browserIntent);
        });

        // Web resources
        Button webButton = getView().findViewById(R.id.open_web_button);
        webButton.setOnClickListener((View v) -> {
            Intent browserIntent =
                    new Intent(Intent.ACTION_VIEW, Uri.parse(webUrl));
            startActivity(browserIntent);
        });

        // Pharmacy details
        TextView pharmacyNameView = getView().findViewById(R.id.pharmacy_name);
        TextView pharmacyPhoneView = getView().findViewById(R.id.pharmacy_phone);
        TextView pharmacistView = getView().findViewById(R.id.pharmacist);
        pharmacyNameView.setText("Name: " + pharmacyName);
        pharmacyPhoneView.setText("Phone: " + pharmacyPhone);
        pharmacistView.setText("Pharmacist: " + pharmacist);
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
