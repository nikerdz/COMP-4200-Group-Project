package com.example.comp4200groupproject;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


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
                        String time = String.format("%02d:%02d", hourOfDay, minute);
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

           if (!event.isEmpty() && !timeOfEvent.isEmpty()) {
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