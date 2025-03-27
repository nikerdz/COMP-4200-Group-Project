package com.example.comp4200groupproject;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteCallbackList;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

public class RemindersActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ReminderAdapter adapter;
    private DatabaseHelper dbHelper;
    private TextView noRemindersTextView, remindersTitle;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);

        // Initialize Views
        recyclerView = findViewById(R.id.recycler_reminders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noRemindersTextView = findViewById(R.id.tv_no_reminders);
        remindersTitle = findViewById(R.id.reminders_title);

        dbHelper = new DatabaseHelper(this);
        List<Reminder> reminderList = dbHelper.getAllReminders();
        adapter = new ReminderAdapter(reminderList);
        recyclerView.setAdapter(adapter);

        loadUserData();

        // Profile popup button
        ImageView btnProfile = findViewById(R.id.btn_profile);
        ImageView btnHome = findViewById(R.id.btn_home);
        btnProfile.setOnClickListener(view -> showProfilePopup(view));
        btnHome.setOnClickListener(view -> {
            startActivity(new Intent(RemindersActivity.this, HomeActivity.class));
        });

        // Show or hide 'No Reminders' message based on data
        if (reminderList.isEmpty()) {
            noRemindersTextView.setVisibility(View.VISIBLE);  // Show "No reminders" message
        } else {
            noRemindersTextView.setVisibility(View.GONE);  // Hide the message
        }

        // FloatingActionButton to add new reminder (now showing dialog)
        FloatingActionButton fab = findViewById(R.id.btn_add_reminder);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddReminderDialog();  // Show dialog instead of new activity
            }
        });

        // Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setItemIconTintList(null);  // Disable tinting
        bottomNav.setSelectedItemId(R.id.nav_reminder); // Highlight current tab
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_todo) {
                startActivity(new Intent(RemindersActivity.this, ToDoActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_planner) {
                startActivity(new Intent(RemindersActivity.this, PlannerActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_notes) {
                startActivity(new Intent(RemindersActivity.this, NotesActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_reminder) {
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
            remindersTitle.setText(name + "'s Reminders");
        }
        cursor.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the reminder list when the activity is resumed
        List<Reminder> reminderList = dbHelper.getAllReminders();
        adapter = new ReminderAdapter(reminderList);
        adapter.setOnItemLongClickListener(reminder -> showEditReminderDialog(reminder));
        recyclerView.setAdapter(adapter);

        // Show or hide 'No Reminders' message based on data
        if (reminderList.isEmpty()) {
            noRemindersTextView.setVisibility(View.VISIBLE);
        } else {
            noRemindersTextView.setVisibility(View.GONE);
        }
    }

    private void showAddReminderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate the custom layout
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_reminder, null);
        EditText etReminderTitle = view.findViewById(R.id.et_reminder);
        EditText etReminderDate = view.findViewById(R.id.reminder_date);

        // Initialize date picker
        final Calendar calendar = Calendar.getInstance();
        final int[] selectedYear = {calendar.get(Calendar.YEAR)};
        final int[] selectedMonth = {calendar.get(Calendar.MONTH)};
        final int[] selectedDay = {calendar.get(Calendar.DAY_OF_MONTH)};

        // Handle date picker button click
        etReminderDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view1, year, month, dayOfMonth) -> {
                        selectedYear[0] = year;
                        selectedMonth[0] = month;
                        selectedDay[0] = dayOfMonth;

                        // Display the selected date in the TextView
                        String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                        etReminderDate.setText(selectedDate);
                    },
                    selectedYear[0], selectedMonth[0], selectedDay[0]
            );
            datePickerDialog.show();
        });

        builder.setView(view);
        builder.setTitle("Add New Reminder");

        // Positive button to save the reminder
        builder.setPositiveButton("Add", (dialog, which) -> {
            String reminderTitle = etReminderTitle.getText().toString().trim();
            String reminderDate = etReminderDate.getText().toString().trim();

            if (!reminderTitle.isEmpty() && !reminderDate.equals("Select Date")) {
                // Add reminder to the database
                DatabaseHelper dbHelper = new DatabaseHelper(this);
                long id = dbHelper.insertReminder(reminderTitle, reminderDate);

                if (id != -1) {
                    notification(id, reminderTitle, reminderDate); // Schedule the notification
                    Toast.makeText(this, "Reminder added successfully!", Toast.LENGTH_SHORT).show();
                    loadReminders();  // Refresh the list
                } else {
                    Toast.makeText(this, "Failed to add reminder.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter both title and date.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Set background color programmatically (after showing the dialog)
        dialog.getWindow().setBackgroundDrawableResource(R.color.bg);  // Use a light color for visibility

        // Ensure buttons have visible text and background
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        if (positiveButton != null) positiveButton.setTextColor(getResources().getColor(R.color.pink));  // Set text color to pink
        if (negativeButton != null) negativeButton.setTextColor(getResources().getColor(R.color.pink));  // Set text color to pink

        if (positiveButton != null) {
            positiveButton.setText("Add");  // Set button text manually
            positiveButton.setAllCaps(false);  // Disable uppercase transformation
        }

        if (negativeButton != null) {
            negativeButton.setText("Cancel");
            negativeButton.setAllCaps(false);  // Disable uppercase transformation
        }

    }

    // Display dialog for editing or deleting a task
    private void showEditReminderDialog(Reminder reminder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate the custom layout
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_reminder, null);
        EditText etReminderTitle = view.findViewById(R.id.et_reminder);
        EditText etReminderDate = view.findViewById(R.id.reminder_date);

        // Pre-fill fields with existing reminder data
        etReminderTitle.setText(reminder.getTitle());
        etReminderDate.setText(reminder.getDate());

        // Initialize date picker
        final Calendar calendar = Calendar.getInstance();
        final int[] selectedYear = {calendar.get(Calendar.YEAR)};
        final int[] selectedMonth = {calendar.get(Calendar.MONTH)};
        final int[] selectedDay = {calendar.get(Calendar.DAY_OF_MONTH)};

        // Handle date picker button click
        etReminderDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view1, year, month, dayOfMonth) -> {
                        selectedYear[0] = year;
                        selectedMonth[0] = month;
                        selectedDay[0] = dayOfMonth;

                        // Display the selected date in the EditText
                        String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                        etReminderDate.setText(selectedDate);
                    },
                    selectedYear[0], selectedMonth[0], selectedDay[0]
            );
            datePickerDialog.show();
        });

        builder.setView(view);
        builder.setTitle("Edit Reminder");

        // Positive button for updating the reminder
        builder.setPositiveButton("Update", (dialog, which) -> {
            String updatedTitle = etReminderTitle.getText().toString().trim();
            String updatedDate = etReminderDate.getText().toString().trim();

            if (!updatedTitle.isEmpty() && !updatedDate.isEmpty()) {
                DatabaseHelper dbHelper = new DatabaseHelper(this);

                // Update the reminder object
                reminder.setTitle(updatedTitle);
                reminder.setDate(updatedDate);

                // Call the update method
                int rowsAffected = dbHelper.updateReminder(reminder);

                if (rowsAffected > 0) {
                    Toast.makeText(this, "Reminder updated successfully!", Toast.LENGTH_SHORT).show();
                    loadReminders();  // Refresh the list
                } else {
                    Toast.makeText(this, "Failed to update reminder.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter both title and date.", Toast.LENGTH_SHORT).show();
            }
        });

        // Neutral Button (Delete)
        builder.setNeutralButton("Delete", (dialog, which) -> {
            dbHelper.deleteReminder(reminder.getId());  // Use the ID to delete the reminder
            loadReminders();  // Refresh the RecyclerView
            Toast.makeText(this, "Reminder deleted!", Toast.LENGTH_SHORT).show();
        });


        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Set background color programmatically
        dialog.getWindow().setBackgroundDrawableResource(R.color.bg);  // Use a light color for visibility

        // Customize button colors
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        if (positiveButton != null) positiveButton.setTextColor(getResources().getColor(R.color.pink));  // Set text color to pink for Save

        if (neutralButton != null) neutralButton.setTextColor(getResources().getColor(R.color.red));  // Set text color to pink for Delete

        if (negativeButton != null) negativeButton.setTextColor(getResources().getColor(R.color.pink));  // Set text color to pink for Cancel

        if (positiveButton != null) {
            positiveButton.setText("Save");  // Set button text manually
            positiveButton.setAllCaps(false);  // Disable uppercase transformation
        }

        if (negativeButton != null) {
            negativeButton.setText("Cancel");
            negativeButton.setAllCaps(false);  // Disable uppercase transformation
        }

        if (neutralButton != null) {
            neutralButton.setText("Delete");
            neutralButton.setAllCaps(false);  // Disable uppercase transformation
        }
    }

    // Method to load all reminders into the RecyclerView
    private void loadReminders() {
        List<Reminder> reminderList = dbHelper.getAllReminders();

        // Show or hide 'No Reminders' message based on data
        if (reminderList.isEmpty()) {
            noRemindersTextView.setVisibility(View.VISIBLE);
        } else {
            noRemindersTextView.setVisibility(View.GONE);
        }

        // Update the RecyclerView with the new reminder list
        adapter = new ReminderAdapter(reminderList);

        // Handle long-press to edit a reminder
        adapter.setOnItemLongClickListener(reminder -> {
            showEditReminderDialog(reminder);  // Open the edit dialog with the selected reminder
        });

        recyclerView.setAdapter(adapter);
    }

    private void notification(long id, String title, String date) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("reminder_id", id);
        intent.putExtra("reminder_title", title);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, (int) id, intent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        String[] parts = date.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]) - 1; // Month is zero-based
        int day = Integer.parseInt(parts[2]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 9, 0, 0); // Default time: 9 AM

        // Set the alarm
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        }
    }


    private void showProfilePopup(View anchorView) {
        View popupView = LayoutInflater.from(this).inflate(R.layout.profile_popup, null);
        PopupWindow popupWindow = new PopupWindow(
                popupView,
                800,
                1200,
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
