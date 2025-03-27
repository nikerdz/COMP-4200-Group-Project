package com.example.comp4200groupproject;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteCallbackList;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
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

import java.util.List;

public class RemindersActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ReminderAdapter adapter;
    private DatabaseHelper dbHelper;
    private TextView noRemindersTextView, remindersTitle;

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

        // FloatingActionButton to add new reminder
        FloatingActionButton fab = findViewById(R.id.btn_add_reminder);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RemindersActivity.this, AddReminderActivity.class));
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
        recyclerView.setAdapter(adapter);

        // Show or hide 'No Reminders' message based on data
        if (reminderList.isEmpty()) {
            noRemindersTextView.setVisibility(View.VISIBLE);
        } else {
            noRemindersTextView.setVisibility(View.GONE);
        }
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
