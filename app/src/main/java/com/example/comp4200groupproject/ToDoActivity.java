package com.example.comp4200groupproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ToDoActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private LinearLayout llTodoList;
    private TextView tvEmptyMessage, todoTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);

        dbHelper = new DatabaseHelper(this);
        llTodoList = findViewById(R.id.ll_todo_list);
        tvEmptyMessage = findViewById(R.id.tv_empty_message);
        todoTitle = findViewById(R.id.tv_todo_title);

        loadUserData();
        loadTodoList();

        // Profile popup button
        ImageView btnProfile = findViewById(R.id.btn_profile);
        ImageView btnHome = findViewById(R.id.btn_home);
        btnProfile.setOnClickListener(view -> showProfilePopup(view));
        btnHome.setOnClickListener(view -> {
            startActivity(new Intent(ToDoActivity.this, HomeActivity.class));
        });

        // Add Task Button
        FloatingActionButton btnAddTask = findViewById(R.id.btn_add_task);
        btnAddTask.setOnClickListener(view -> showAddTaskDialog());

        // Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setItemIconTintList(null);  // Disable tinting
        bottomNav.setSelectedItemId(R.id.nav_todo); // Highlight current tab
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_todo) {
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

    // ✅ Load and display all tasks
    private void loadTodoList() {
        llTodoList.removeAllViews();
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT * FROM todo", null);

        if (cursor.getCount() == 0) {
            tvEmptyMessage.setVisibility(View.VISIBLE);
        } else {
            tvEmptyMessage.setVisibility(View.GONE);

            int taskIndex = 0;  // Initialize task index for alternating background color

            while (cursor.moveToNext()) {
                String taskId = cursor.getString(cursor.getColumnIndexOrThrow("id"));
                String taskDescription = cursor.getString(cursor.getColumnIndexOrThrow("task"));
                boolean isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow("is_completed")) == 1;

                // Inflate task item layout
                View taskView = getLayoutInflater().inflate(R.layout.todo_item, null);
                TextView tvTaskDescription = taskView.findViewById(R.id.tv_task_description);
                CheckBox cbTask = taskView.findViewById(R.id.cb_task);

                cbTask.setChecked(isCompleted);
                tvTaskDescription.setText(taskDescription);

                // Set alternating background color based on task index
                if (taskIndex % 2 == 0) {
                    taskView.setBackgroundColor(getResources().getColor(R.color.grey));  // Even tasks - Light background
                } else {
                    taskView.setBackgroundColor(getResources().getColor(R.color.bg));  // Odd tasks - Dark background
                }

                // Handle checkbox state changes (mark as complete/incomplete)
                cbTask.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    dbHelper.updateTaskCompletionStatus(taskId, isChecked);
                });

                // Long-press to edit or delete task
                taskView.setOnLongClickListener(v -> {
                    showEditTaskDialog(taskId, taskDescription);
                    return true;
                });

                llTodoList.addView(taskView);

                taskIndex++;  // Increment task index
            }
        }
        cursor.close();
    }


    // ✅ Load the user's name and display it in the title
    private void loadUserData() {
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT name FROM users ORDER BY id DESC LIMIT 1", null);
        if (cursor.moveToFirst()) {
            String name = cursor.getString(0);
            todoTitle.setText(name + "'s To-Do List");
        }
        cursor.close();
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate the custom layout
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null);
        EditText etTask = view.findViewById(R.id.et_task);

        builder.setView(view);
        builder.setTitle("Add New Task");  // Ensure the title is set
        builder.setPositiveButton("Add", (dialog, which) -> {
            String task = etTask.getText().toString().trim();
            if (!task.isEmpty()) {
                dbHelper.addTask(task);
                loadTodoList();
                Toast.makeText(this, "Task added successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Please enter a task.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // ✅ Create the dialog separately to customize it
        AlertDialog dialog = builder.create();

        // 🌟 Show the dialog first so we can access the buttons
        dialog.show();

        // Set background color programmatically (after showing the dialog)
        dialog.getWindow().setBackgroundDrawableResource(R.color.bg);  // Use a light color for visibility

        // Ensure buttons have visible text and background
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        if (positiveButton != null) {
            positiveButton.setTextColor(getResources().getColor(R.color.pink));  // Set text color to pink
        }

        if (negativeButton != null) {
            negativeButton.setTextColor(getResources().getColor(R.color.pink));  // Set text color to pink
        }
    }

    // ✅ Display dialog for editing or deleting a task
    private void showEditTaskDialog(String taskId, String currentDescription) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate the custom layout for Edit Task
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_task, null);
        EditText etTask = view.findViewById(R.id.et_task);
        etTask.setText(currentDescription);

        builder.setView(view);
        builder.setTitle("Edit Task");

        // Positive Button (Save)
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newDescription = etTask.getText().toString().trim();
            if (!newDescription.isEmpty()) {
                dbHelper.updateTask(Integer.parseInt(taskId), newDescription);
                loadTodoList();
                Toast.makeText(this, "Task updated!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Task cannot be empty.", Toast.LENGTH_SHORT).show();
            }
        });

        // Neutral Button (Delete)
        builder.setNeutralButton("Delete", (dialog, which) -> {
            dbHelper.deleteTask(Integer.parseInt(taskId));
            loadTodoList();
            Toast.makeText(this, "Task deleted!", Toast.LENGTH_SHORT).show();
        });

        // Negative Button (Cancel)
        builder.setNegativeButton("Cancel", null);

        // Create the dialog
        AlertDialog dialog = builder.create();

        // Show the dialog
        dialog.show();

        // Set background color programmatically
        dialog.getWindow().setBackgroundDrawableResource(R.color.bg);  // Use a light color for visibility

        // Customize button colors
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        if (positiveButton != null) {
            positiveButton.setTextColor(getResources().getColor(R.color.pink));  // Set text color to pink for Save
        }

        if (neutralButton != null) {
            neutralButton.setTextColor(getResources().getColor(R.color.red));  // Set text color to pink for Delete
        }

        if (negativeButton != null) {
            negativeButton.setTextColor(getResources().getColor(R.color.pink));  // Set text color to pink for Cancel
        }
    }

    // ✅ Display the Profile Popup anchored to the profile button
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
