package com.example.comp4200groupproject;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PlannerActivity extends AppCompatActivity {

    private TextView plannerTitle, emptyMessage;
    private CalendarView calendarView;
    private RecyclerView rvEventList;
    private PlannerAdapter eventAdapter;
    private List<PlannerEvent> eventList;
    private DatabaseHelper dbHelper;
    private Calendar calendar;
    private String currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner);

        dbHelper = new DatabaseHelper(this);
        calendarView = findViewById(R.id.calendarView);
        rvEventList = findViewById(R.id.rv_event_list);
        calendar = Calendar.getInstance();
        currentDate = formatDate(calendar.getTimeInMillis());
        plannerTitle = findViewById(R.id.planner_title);
        emptyMessage = findViewById(R.id.tv_empty_message);

        eventList = new ArrayList<>();
        eventAdapter = new PlannerAdapter(eventList, dbHelper);
        rvEventList.setLayoutManager(new LinearLayoutManager(this));
        rvEventList.setAdapter(eventAdapter);

        calendarView = findViewById(R.id.calendarView);
        ImageButton addButton = findViewById(R.id.btn_add_event);

        loadUserData();
        loadEvents(currentDate);

        // Profile popup button
        ImageView btnProfile = findViewById(R.id.btn_profile);
        ImageView btnHome = findViewById(R.id.btn_home);
        btnProfile.setOnClickListener(view -> showProfilePopup(view));
        btnHome.setOnClickListener(view -> {
            startActivity(new Intent(PlannerActivity.this, HomeActivity.class));
        });

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            currentDate = formatDate(calendar.getTimeInMillis());
            loadEvents(currentDate);
        });

        findViewById(R.id.btn_add_event).setOnClickListener(v -> addCalendarEvent());
        addButton.setOnClickListener(v -> addCalendarEvent());

        // Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setItemIconTintList(null);  // Disable tinting
        bottomNav.setSelectedItemId(R.id.nav_planner); // Highlight current tab
        bottomNav.setOnItemSelectedListener(item -> {
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
        });
    }

    // Load the user's name and display it in the title
    private void loadUserData() {
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT name FROM users ORDER BY id DESC LIMIT 1", null);
        if (cursor.moveToFirst()) {
            String name = cursor.getString(0);
            plannerTitle.setText(name + "'s Planner");
        }
        cursor.close();
    }

    private void addCalendarEvent() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate the custom layout
        View view = LayoutInflater.from(this).inflate(R.layout.add_event_dialog, null);
        EditText eventName = view.findViewById(R.id.eventName);
        EditText eventTime = view.findViewById(R.id.EventTime);

        // Time Picker Logic
        eventTime.setFocusable(false);
        eventTime.setClickable(true);
        eventTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    PlannerActivity.this,
                    (view1, hourOfDay, minute) -> {
                        String time;
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
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

        // Set the layout in the builder
        builder.setView(view);
        builder.setTitle("Add New Event");

        // Add Button Click Logic
        builder.setPositiveButton("Save", (dialog, which) -> {
            String event = eventName.getText().toString().trim();
            String timeOfEvent = eventTime.getText().toString().trim();
            String date = formatDate(calendar.getTimeInMillis());

            if (!event.isEmpty() && !timeOfEvent.isEmpty() && !date.isEmpty()) {
                // Create a new PlannerEvent with the title, time, and date
                PlannerEvent newEvent = new PlannerEvent(event, timeOfEvent, date);

                // Insert the event and get the generated ID from the database
                long result = dbHelper.insertEvent(newEvent);

                if (result != -1) {
                    // After inserting, set the ID of the event (since it's auto-generated by the database)
                    newEvent.setId((int) result);

                    // Add the new event to the list and notify the adapter
                    eventList.add(newEvent);
                    eventAdapter.notifyItemInserted(eventList.size() - 1);
                    Toast.makeText(this, "Event added successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to add event", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Set the background color after showing the dialog
        dialog.getWindow().setBackgroundDrawableResource(R.color.bg);

        // Customize button text colors
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        if (positiveButton != null) {
            positiveButton.setTextColor(getResources().getColor(R.color.pink));  // Pink color for "Save"
        }

        if (negativeButton != null) {
            negativeButton.setTextColor(getResources().getColor(R.color.pink));  // Pink color for "Cancel"
        }
    }

    private void loadEvents(String date) {
        eventList.clear();
        eventList.addAll(dbHelper.getAllEvents(date));
        eventAdapter.notifyDataSetChanged();


    }

    private String formatDate(long dateInMillis) {
        return new SimpleDateFormat("yyyy-MM-dd").format(dateInMillis);
    }

    // Display the Profile Popup anchored to the profile button
    private void showProfilePopup(View anchorView) {
        View popupView = LayoutInflater.from(this).inflate(R.layout.profile_popup, null);
        PopupWindow popupWindow = new PopupWindow(
                popupView,
                800,  // Width
                1200,  // Height
                true
        );

        TextView tvProfileName = popupView.findViewById(R.id.tv_profile_name);
        TextView tvProfileEducation = popupView.findViewById(R.id.tv_profile_education);

        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT name, education FROM users ORDER BY id DESC LIMIT 1", null);

        if (cursor.moveToFirst()) {
            String name = cursor.getString(0);
            String education = cursor.getString(1);
            tvProfileName.setText(name);
            tvProfileEducation.setText(education);
        } else {
            Toast.makeText(this, "No user data found.", Toast.LENGTH_SHORT).show();
        }

        cursor.close();
        popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
    }
}