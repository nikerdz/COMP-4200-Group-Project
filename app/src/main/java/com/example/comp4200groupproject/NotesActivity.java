package com.example.comp4200groupproject;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class NotesActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private TextView notesTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        dbHelper = new DatabaseHelper(this);
        notesTitle = findViewById(R.id.notes_title);

        loadUserData();

        // Handle FAB click to show Add Note dialog
        FloatingActionButton fabAddNote = findViewById(R.id.btn_add_note);
        fabAddNote.setOnClickListener(view -> showAddNoteDialog());

        // Handle Bottom Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setItemIconTintList(null);  // Disable tinting
        bottomNav.setSelectedItemId(R.id.nav_notes);  // Highlight current tab
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_todo) {
                startActivity(new Intent(this, ToDoActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_planner) {
                startActivity(new Intent(this, PlannerActivity.class));
                return true;
            } else if (item.getItemId() == R.id.nav_notes) {
                return true;
            } else if (item.getItemId() == R.id.nav_reminder) {
                startActivity(new Intent(this, RemindersActivity.class));
                return true;
            }
            return false;
        });

        // Handle Top Navigation (Home and Profile)
        ImageView btnHome = findViewById(R.id.btn_home);
        ImageView btnProfile = findViewById(R.id.btn_profile);

        btnHome.setOnClickListener(view -> {
            startActivity(new Intent(this, HomeActivity.class));
        });

        btnProfile.setOnClickListener(this::showProfilePopup);

        loadNotes();
    }

    // Load the user's name and display it in the title
    private void loadUserData() {
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT name FROM users ORDER BY id DESC LIMIT 1", null);
        if (cursor.moveToFirst()) {
            String name = cursor.getString(0);
            notesTitle.setText(name + "'s Notes");
        }
        cursor.close();
    }

    // Method to show the "Add Note" dialog box
    private void showAddNoteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate the custom layout
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_note, null);
        EditText input = view.findViewById(R.id.et_note_content);

        builder.setView(view);
        builder.setTitle("Add Note");

        builder.setPositiveButton("Add", (dialog, which) -> {
            String noteText = input.getText().toString().trim();
            if (!noteText.isEmpty()) {
                dbHelper.addNote(noteText);
                Toast.makeText(this, "Note added", Toast.LENGTH_SHORT).show();
                loadNotes();
            } else {
                Toast.makeText(this, "Note cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Set the background color programmatically after displaying the dialog
        dialog.getWindow().setBackgroundDrawableResource(R.color.bg);

        // Customize button text colors
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        if (positiveButton != null) {
            positiveButton.setTextColor(getResources().getColor(R.color.pink));  // Pink for "Add"
        }

        if (negativeButton != null) {
            negativeButton.setTextColor(getResources().getColor(R.color.pink));  // Pink for "Cancel"
        }
    }


    // Method to show the "Edit Note" dialog box with consistent styling
    void showEditNoteDialog(Note note) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Inflate the custom layout
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_note, null);
        EditText input = view.findViewById(R.id.et_note_content);
        input.setText(note.getContent());  // Pre-fill with the current note content

        builder.setView(view);
        builder.setTitle("Edit Note");

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newContent = input.getText().toString().trim();
            if (!newContent.isEmpty()) {
                dbHelper.updateNote(note.getId(), newContent);
                Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show();
                loadNotes();
            } else {
                Toast.makeText(this, "Note content cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        // Neutral Button (Delete)
        builder.setNeutralButton("Delete", (dialog, which) -> {
            dbHelper.deleteNote(note.getId());   // Delete the note
            //originalList.remove(note);
            //displayList.remove(note);
            //notifyItemRemoved(position);
            Toast.makeText(this, "Note deleted!", Toast.LENGTH_SHORT).show();
            loadNotes();  // Refresh the RecyclerView
        });


        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Set the background color programmatically
        dialog.getWindow().setBackgroundDrawableResource(R.color.bg);

        // Customize button text colors
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button neutralButton = dialog.getButton(android.app.AlertDialog.BUTTON_NEUTRAL);
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        if (positiveButton != null) {
            positiveButton.setTextColor(getResources().getColor(R.color.pink));  // Pink for "Save"
        }

        if (neutralButton != null) {
            negativeButton.setTextColor(getResources().getColor(R.color.red));
        }

        if (negativeButton != null) {
            negativeButton.setTextColor(getResources().getColor(R.color.pink));  // Pink for "Cancel"
        }
    }

    private void loadNotes() {
        List<Note> notes = dbHelper.getAllNotes();

        TextView tvNoNotes = findViewById(R.id.tv_no_notes);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewNotes);

        if (notes.isEmpty()) {
            // Show "No notes found" message and hide RecyclerView
            tvNoNotes.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            // Hide message and show RecyclerView
            tvNoNotes.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            // Pass the activity reference to the adapter
            NotesAdapter adapter = new NotesAdapter(notes, dbHelper, this);
            recyclerView.setAdapter(adapter);

            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
            recyclerView.setLayoutManager(gridLayoutManager);

            // Add spacing between tiles
            recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, RecyclerView parent, @NonNull RecyclerView.State state) {
                    outRect.set(16, 16, 16, 16);
                }
            });
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
