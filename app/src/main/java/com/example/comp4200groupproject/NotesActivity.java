package com.example.comp4200groupproject;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class NotesActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private NotesAdapter notesAdapter;
    private EditText editTextNote, editTextSearch;
    private RecyclerView recyclerView;
    private List<Note> notesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        dbHelper = new DatabaseHelper(this);

        recyclerView = findViewById(R.id.recyclerViewNotes);
        editTextNote = findViewById(R.id.editTextNote);
        editTextSearch = findViewById(R.id.editTextSearch);
        Button buttonAddNote = findViewById(R.id.buttonAddNote);
        Button buttonSort = findViewById(R.id.buttonSort);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadNotes();

        buttonAddNote.setOnClickListener(v -> {
            String noteText = editTextNote.getText().toString().trim();
            if (!noteText.isEmpty()) {
                dbHelper.addNote(noteText);
                editTextNote.setText("");
                loadNotes();
            } else {
                Toast.makeText(this, "Please enter note content", Toast.LENGTH_SHORT).show();
            }
        });

        buttonSort.setOnClickListener(v -> {
            notesList.sort((a, b) -> a.getContent().compareToIgnoreCase(b.getContent()));
            notesAdapter.updateList(notesList, editTextSearch.getText().toString().trim());
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                notesAdapter.updateList(notesList, s.toString());
            }
        });
    }

    private void loadNotes() {
        notesList = dbHelper.getAllNotes();
        notesAdapter = new NotesAdapter(notesList, dbHelper);
        recyclerView.setAdapter(notesAdapter);
    }
}
