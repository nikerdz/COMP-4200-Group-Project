package com.example.comp4200groupproject;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

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
        adapter = new PlannerAdapter(eventList);

        calendarView = findViewById(R.id.calendarView);
        addButton = findViewById(R.id.addButton);
        recyclerView = findViewById(R.id.recyclerview);
        calendar = Calendar.getInstance();
        calendarView.setDate(calendar.getTimeInMillis());;

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            date = dayOfMonth + "/" + (month + 1) + "/" + year;
            showCalendarEvents(date);
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
                    }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
                    false
            );
            timePickerDialog.setTitle("Select Time");
            timePickerDialog.show();
        });

        btnSave.setOnClickListener(v -> {
            String event = eventName.getText().toString();
            String timeOfEvent = eventTime.getText().toString();
            String date = calendarView.getDate() + "";

           if (!event.isEmpty() && !timeOfEvent.isEmpty() && !date.isEmpty()) {
               dbHelper.insertEvent(event, timeOfEvent, date);
               eventList.add(new PlannerEvent(event, timeOfEvent));
               adapter.notifyDataSetChanged();
           }
            dialog.dismiss();
            showCalendarEvents(date);

        });
        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });



    }

    private void showCalendarEvents(String date) {
        if(dbHelper == null){
            return;
        }
        eventList.clear();
        eventList.addAll(dbHelper.getAllEvents(date));
        adapter.notifyDataSetChanged();

    }


}