package com.example.comp4200groupproject;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

    }

    private void showCalendarEvents(String date) {
        String events = sharedPreferences.getString(date, "");
        String[] eventArray = events.split(",");

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, eventArray);
        listView.setAdapter(adapter);
    }
}