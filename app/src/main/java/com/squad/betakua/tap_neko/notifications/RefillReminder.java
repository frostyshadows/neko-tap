package com.squad.betakua.tap_neko.notifications;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import com.squad.betakua.tap_neko.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RefillReminder extends AppCompatActivity {

    //Formatting the refill reminder date
    Date currentDate = new Date();
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat reminderFormat = new SimpleDateFormat("dd/MM/yyyy");
    Date reminderDate = new Date();


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refill_reminder);

        TextView dateText = findViewById(R.id.dateText);
        Button twoWeeks = findViewById(R.id.twoWeeksButton);
        Button thirtyDays = findViewById(R.id.thirtyDaysButton);
        Button ninetyDays = findViewById(R.id.ninetyDaysButton);
        CalendarView calendarView = findViewById(R.id.calenderView);

        calendar.setTime(currentDate);

        //set date to date selected on calendar, current date by default
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange( CalendarView view, int year, int month, int dayOfMonth) {
                dateText.setText(dayOfMonth+"/"+(month+1)+"/"+year);
            }
        });

        twoWeeks.setOnClickListener((View view) -> {
            calendar.add(Calendar.DATE, 14);
            reminderDate = calendar.getTime();
            dateText.setText(reminderFormat.format(reminderDate));
        });

        thirtyDays.setOnClickListener((View view) -> {
            calendar.add(Calendar.DATE, 30);
            reminderDate = calendar.getTime();
            dateText.setText(reminderFormat.format(reminderDate));
        });

        ninetyDays.setOnClickListener((View view) -> {
            calendar.add(Calendar.DATE, 90);
            reminderDate = calendar.getTime();
            dateText.setText(reminderFormat.format(reminderDate));
        });
    }
}
