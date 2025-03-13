package com.example.comp4200groupproject;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;


import java.util.Calendar;

public class PlannerActivity extends AppCompatActivity {
String date;
ListView listView;
ImageButton addButton;
CalendarView calendarView;
Calendar calendar;
SharedPreferences sharedPreferences;
ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner);
        sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);

        calendarView = findViewById(R.id.calendarView);
        listView = findViewById(R.id.listView);
        addButton = findViewById(R.id.addButton);

        calendar = Calendar.getInstance();
        calendarView.setDate(calendar.getTimeInMillis());;

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            date = dayOfMonth + "/" + (month + 1) + "/" + year;
            showCalendarEvents(date);
        });
        addButton.setOnClickListener(v -> {
           addCalendarEvent();
        });

    }

    private void addCalendarEvent() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_event_dialog);
        dialog.show();
        EditText eventName = dialog.findViewById(R.id.eventName);
        EditText eventTime = dialog.findViewById(R.id.EventTime);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnSave = dialog.findViewById(R.id.btnSave);

        btnSave.setOnClickListener(v -> {
            String event = eventName.getText().toString();
            String time = eventTime.getText().toString();

           String events = sharedPreferences.getString(date, "");

            if (events.isEmpty()) {
                events = event + " " + time;
            } else {
                events += event + " " + time + ",";
            }
            sharedPreferences.edit().putString(date, events).apply();
            showCalendarEvents(date);
            dialog.dismiss();

        });
        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });



    }

    private void showCalendarEvents(String date) {
        String events = sharedPreferences.getString(date, "");
        String[] eventArray = events.split(",");

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, eventArray);
        listView.setAdapter(adapter);
    }
}