package com.example.comp4200groupproject;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        dbHelper = new DatabaseHelper(this);

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

    // Method to show the "Add Note" dialog box
    private void showAddNoteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Note");

        final EditText input = new EditText(this);
        input.setHint("Enter note content");
        builder.setView(input);

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

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // Method to show the "Edit Note" dialog box
    private void showEditNoteDialog(Note note) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Note");

        final EditText input = new EditText(this);
        input.setText(note.getContent());
        builder.setView(input);

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

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
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

            NotesAdapter adapter = new NotesAdapter(notes, dbHelper);
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
