package com.example.comp4200groupproject;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ToDoActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);

        dbHelper = new DatabaseHelper(this);

        // Profile popup button
        ImageView btnProfile = findViewById(R.id.btn_profile);
        ImageView btnHome = findViewById(R.id.btn_home);
        btnProfile.setOnClickListener(view -> showProfilePopup(view));  // Pass the view for anchoring
        btnHome.setOnClickListener(view -> {
            // Create an Intent to navigate to HomeActivity
            startActivity(new Intent(ToDoActivity.this, HomeActivity.class));
        });


        // Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setItemIconTintList(null);  // Disable tinting

        // Clear the selection manually when the activity starts
        // Set an item ID to null on the BottomNavigationView (remove default selection)
        bottomNav.setSelectedItemId(R.id.nav_todo); // Make sure the default item is not selected
        bottomNav.setOnItemSelectedListener(item -> {
            // Check the item selected and handle the navigation
            if (item.getItemId() == R.id.nav_todo) {
                startActivity(new Intent(ToDoActivity.this, ToDoActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_planner) {
                startActivity(new Intent(ToDoActivity.this, PlannerActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_notes) {
                startActivity(new Intent(ToDoActivity.this, NotesActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_reminder) {
                startActivity(new Intent(ToDoActivity.this, RemindersActivity.class));
                return true;
            }
            return false;
        });
    }

    // ✅ Method to display the Profile Popup anchored to the profile button
    private void showProfilePopup(View anchorView) {
        // Inflate the popup layout
        View popupView = LayoutInflater.from(this).inflate(R.layout.profile_popup, null);

        // Create PopupWindow
        PopupWindow popupWindow = new PopupWindow(
                popupView,
                800,  // Width
                1200,  // Height
                true
        );

        // Get references to TextViews inside the popup
        TextView tvProfileName = popupView.findViewById(R.id.tv_profile_name);
        TextView tvProfileEducation = popupView.findViewById(R.id.tv_profile_education);

        // Load user info from SQLite DB (get the most recent user by ordering by id DESC)
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT name, education FROM users ORDER BY id DESC LIMIT 1", null);

        if (cursor.moveToFirst()) {
            String name = cursor.getString(0);
            String education = cursor.getString(1);

            // Display the name and education in the popup
            tvProfileName.setText(name);
            tvProfileEducation.setText(education);
        } else {
            Toast.makeText(this, "No user data found.", Toast.LENGTH_SHORT).show();
        }

        cursor.close();

        // ✅ Show the popup in the center of the screen
        // Use the parent layout to position the popup in the center of the screen
        popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
    }
}