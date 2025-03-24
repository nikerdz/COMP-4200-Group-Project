package com.example.comp4200groupproject;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PlannerActivity extends AppCompatActivity {
String date;
ImageButton addButton;
CalendarView calendarView;
Calendar calendar;
PlannerAdapter adapter;
RecyclerView recyclerView;
private DatabaseHelper dbHelper;
private List<PlannerEvent> eventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner);
        BottomNavigationView bottomNav = findViewById(R.id.btm_nav);
        bottomNav.setItemIconTintList(null);  // Disable tinting
        dbHelper = new DatabaseHelper(this);
        eventList = new ArrayList<>();
        adapter = new PlannerAdapter(eventList, dbHelper);

        calendarView = findViewById(R.id.calendarView);
        addButton = findViewById(R.id.addButton);
        recyclerView = findViewById(R.id.recyclerview);
        calendar = Calendar.getInstance();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        showCalendarEvents();

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                calendar.set(year, month, dayOfMonth);
                showCalendarEvents();
            }
        });


        addButton.setOnClickListener(v -> {
           addCalendarEvent();
        });

        bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_todo) {
                    startActivity(new Intent(PlannerActivity.this, ToDoActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.nav_planner) {
                    return true;
                } else if (item.getItemId() == R.id.nav_notes) {
                    startActivity(new Intent(PlannerActivity.this, NotesActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.nav_reminder) {
                    startActivity(new Intent(PlannerActivity.this, RemindersActivity.class));
                    return true;
                }

                return false;
            }
        });
    }

    private void addCalendarEvent() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_event_dialog);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.show();

        EditText eventName = dialog.findViewById(R.id.eventName);
        EditText eventTime = dialog.findViewById(R.id.EventTime);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        Button btnSave = dialog.findViewById(R.id.btnSave);

        eventTime.setFocusable(false);
        eventTime.setClickable(true);

        eventTime.setOnClickListener(view -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    PlannerActivity.this,
                    (view1, hourOfDay, minute) -> {
                        String time;
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a");
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        time = simpleDateFormat.format(calendar.getTime());
                        eventTime.setText(time);
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
            );
            timePickerDialog.setTitle("Select Time");
            timePickerDialog.show();
        });

        btnSave.setOnClickListener(v -> {
            String event = eventName.getText().toString();
            String timeOfEvent = eventTime.getText().toString();
            String date = formatDate(calendar.getTimeInMillis());

            if (!event.isEmpty() && !timeOfEvent.isEmpty() && !date.isEmpty()) {
                dbHelper.insertEvent(event, timeOfEvent, date);
                eventList.add(new PlannerEvent(event, timeOfEvent, date));
                adapter.notifyDataSetChanged();
            }
            dialog.dismiss();
            showCalendarEvents();
        });

        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    private void showCalendarEvents() {
        eventList.clear();
        String date = formatDate(calendar.getTimeInMillis());
        eventList.addAll(dbHelper.getAllEvents(date));
        adapter.notifyDataSetChanged();
    }

    private String formatDate(long dateInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(dateInMillis);
    }




}