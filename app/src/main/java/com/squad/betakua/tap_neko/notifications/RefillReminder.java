package com.squad.betakua.tap_neko.notifications;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.squad.betakua.tap_neko.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RefillReminder extends AppCompatActivity {

    //Formatting the refill reminder date
    Date reminderDate = Calendar.getInstance().getTime();
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat reminderFormat = new SimpleDateFormat("dd-MMM-yyyy");
    String reminderString = reminderFormat.format(reminderDate);



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refill_reminder);
//        TextView dateText = findViewById(R.id.dateText);
//        Button twoWeeks = findViewById(R.id.twoWeeksButton);
//
//        if (twoWeeks.isPressed()){
//            try {
//                calendar.setTime(reminderFormat.parse(reminderString));
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            calendar.add(Calendar.DAY_OF_MONTH, 14);
//            dateText.setText(reminderFormat.format(calendar.getTime()));
//        }
    }


}
