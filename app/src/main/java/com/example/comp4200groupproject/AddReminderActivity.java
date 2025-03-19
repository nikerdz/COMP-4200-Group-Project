package com.example.comp4200groupproject;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class AddReminderActivity extends AppCompatActivity {
    private EditText editTextReminder;
    private TextView textViewDate;
    private Button buttonSelectDate, buttonSave;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        editTextReminder = findViewById(R.id.editTextReminder);
        textViewDate = findViewById(R.id.textViewDate);
        buttonSelectDate = findViewById(R.id.buttonSelectDate);
        buttonSave = findViewById(R.id.buttonSave);

        buttonSelectDate.setOnClickListener(v -> showDatePicker());
        buttonSave.setOnClickListener(v -> saveReminder());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, y, m, d) -> {
            selectedDate = y + "-" + (m + 1) + "-" + d;
            textViewDate.setText(selectedDate);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void saveReminder() {
        String reminderText = editTextReminder.getText().toString().trim();
        String date = textViewDate.getText().toString();

        if (reminderText.isEmpty() || date.equals("Select Date")) {
            Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        long id = dbHelper.insertReminder(reminderText, date);

        if (id != -1) {
            notification(id, reminderText, date);
            Toast.makeText(this, "Reminder saved!", Toast.LENGTH_SHORT).show();
            finish(); // Close activity
        } else {
            Toast.makeText(this, "Error saving reminder", Toast.LENGTH_SHORT).show();
        }
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
        int month = Integer.parseInt(parts[1]) - 1;
        int day = Integer.parseInt(parts[2]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 9, 0, 0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

}
