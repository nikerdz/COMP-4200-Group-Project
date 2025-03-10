package com.example.comp4200groupproject;

import android.os.Bundle;
import android.widget.CalendarView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class PlannerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner);

        CalendarView calendarView = findViewById(R.id.calendarView);

        Calendar calendar = Calendar.getInstance();
        calendarView.setDate(calendar.getTimeInMillis());;
    }
}