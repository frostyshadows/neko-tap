package com.squad.betakua.tap_neko.notifications;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refill_reminder);

        TextView dateText = findViewById(R.id.dateText);
        Button twoWeeks = findViewById(R.id.twoWeeksButton);
        Button thirtyDays = findViewById(R.id.thirtyDaysButton);
        Button ninetyDays = findViewById(R.id.ninetyDaysButton);

        calendar.setTime(currentDate);

        twoWeeks.setOnClickListener((View view) -> {
            calendar.add(Calendar.DATE, 14);
            Date reminderDate = calendar.getTime();
            dateText.setText(reminderFormat.format(reminderDate));
        });

        thirtyDays.setOnClickListener((View view) -> {
            calendar.add(Calendar.DATE, 30);
            Date reminderDate = calendar.getTime();
            dateText.setText(reminderFormat.format(reminderDate));
        });

        ninetyDays.setOnClickListener((View view) -> {
            calendar.add(Calendar.DATE, 90);
            Date reminderDate = calendar.getTime();
            dateText.setText(reminderFormat.format(reminderDate));
        });
    }
}
