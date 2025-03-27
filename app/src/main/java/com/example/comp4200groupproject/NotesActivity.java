package com.example.comp4200groupproject;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotesActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private RecyclerView recyclerView;
    private NotesAdapter notesAdapter;
    private TextView notesTitle;
    private EditText editTextSearch;
    private List<Note> allNotes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        dbHelper = new DatabaseHelper(this);
        notesTitle = findViewById(R.id.notes_title);
        editTextSearch = findViewById(R.id.editTextSearch);

        recyclerView = findViewById(R.id.recyclerViewNotes);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.set(16, 16, 16, 16);
            }
        });

        // Set up adapter
        notesAdapter = new NotesAdapter(allNotes, dbHelper, this);
        recyclerView.setAdapter(notesAdapter);

        loadUserData();
        loadNotes();

        FloatingActionButton fabAddNote = findViewById(R.id.btn_add_note);
        fabAddNote.setOnClickListener(view -> showAddNoteDialog());

        Button buttonSort = findViewById(R.id.buttonSort);
        buttonSort.setOnClickListener(v -> {
            Collections.sort(allNotes, (a, b) -> a.getContent().compareToIgnoreCase(b.getContent()));
            notesAdapter.updateList(allNotes, editTextSearch.getText().toString());
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                notesAdapter.updateList(allNotes, s.toString());
            }
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setItemIconTintList(null);
        bottomNav.setSelectedItemId(R.id.nav_notes);
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

        ImageView btnHome = findViewById(R.id.btn_home);
        ImageView btnProfile = findViewById(R.id.btn_profile);

        btnHome.setOnClickListener(view -> startActivity(new Intent(this, HomeActivity.class)));
        btnProfile.setOnClickListener(this::showProfilePopup);
    }

    private void loadUserData() {
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT name FROM users ORDER BY id DESC LIMIT 1", null);
        if (cursor.moveToFirst()) {
            String name = cursor.getString(0);
            notesTitle.setText(name + "'s Notes");
        }
        cursor.close();
    }

    private void showAddNoteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getWindow().setBackgroundDrawableResource(R.color.tan);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.pink));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.pink));
    }

    public void showEditNoteDialog(Note note) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_note, null);
        EditText input = view.findViewById(R.id.et_note_content);
        input.setText(note.getContent());

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

        builder.setNeutralButton("Delete", (dialog, which) -> {
            dbHelper.deleteNote(note.getId());
            Toast.makeText(this, "Note deleted!", Toast.LENGTH_SHORT).show();
            loadNotes();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getWindow().setBackgroundDrawableResource(R.color.tan);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.pink));
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.red));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.pink));
    }

    private void loadNotes() {
        allNotes = dbHelper.getAllNotes();
        TextView tvNoNotes = findViewById(R.id.tv_no_notes);
        if (allNotes.isEmpty()) {
            tvNoNotes.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvNoNotes.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            notesAdapter.updateList(allNotes, editTextSearch.getText().toString());
        }
    }

    private void showProfilePopup(View anchorView) {
        View popupView = LayoutInflater.from(this).inflate(R.layout.profile_popup, null);
        PopupWindow popupWindow = new PopupWindow(popupView, 800, 1200, true);
        TextView tvProfileName = popupView.findViewById(R.id.tv_profile_name);
        TextView tvProfileEducation = popupView.findViewById(R.id.tv_profile_education);
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("SELECT name, education FROM users ORDER BY id DESC LIMIT 1", null);
        if (cursor.moveToFirst()) {
            tvProfileName.setText(cursor.getString(0));
            tvProfileEducation.setText(cursor.getString(1));
        }
        cursor.close();
        popupWindow.showAtLocation(findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
    }
}
