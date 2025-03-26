package com.example.comp4200groupproject;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText editName;
    private Spinner spinnerEducation;
    private Button btnContinue;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Views
        editName = findViewById(R.id.edit_name);
        spinnerEducation = findViewById(R.id.spinnerEducation);
        btnContinue = findViewById(R.id.btn_continue);

        // Initialize the spinner
        Spinner spinnerEducation = findViewById(R.id.spinnerEducation);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.education_levels, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEducation.setAdapter(adapter);

        // Initialize SQLite DB Helper
        databaseHelper = new DatabaseHelper(this);

        // Continue button logic
        btnContinue.setOnClickListener(view -> {
            String name = editName.getText().toString().trim();
            String education = spinnerEducation.getSelectedItem().toString();

            // Validate that the user doesn't select the placeholder
            if (!name.isEmpty() && !education.equals("What is your Education Level?")) {
                saveUserData(name, education);
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(MainActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to save data to SQLite DB
    private void saveUserData(String name, String education) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("education", education);

        long rowId = db.insert("users", null, values);

        if (rowId != -1) {
            Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to Save Data", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }
}
