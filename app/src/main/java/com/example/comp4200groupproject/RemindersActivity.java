package com.example.comp4200groupproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class RemindersActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ReminderAdapter adapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);

        recyclerView = findViewById(R.id.recycler_reminders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DatabaseHelper(this);
        List<Reminder> reminderList = dbHelper.getAllReminders();
        adapter = new ReminderAdapter(reminderList);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.btn_add_reminder);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RemindersActivity.this, AddReminderActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter = new ReminderAdapter(dbHelper.getAllReminders());
        recyclerView.setAdapter(adapter);
    }
}
