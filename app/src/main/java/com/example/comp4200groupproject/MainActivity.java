package com.example.comp4200groupproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setItemIconTintList(null);  // Disable tinting


        // Use the updated method for item selection
        bottomNav.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_todo) {
                    startActivity(new Intent(MainActivity.this, ToDoActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.nav_planner) {
                    startActivity(new Intent(MainActivity.this, RemindersActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.nav_notes) {
                    startActivity(new Intent(MainActivity.this, NotesActivity.class));
                    return true;
                } else if (item.getItemId() == R.id.nav_reminder) {
                    startActivity(new Intent(MainActivity.this, RemindersActivity.class));
                    return true;
                }

                return false;
            }
        });
    }
}
