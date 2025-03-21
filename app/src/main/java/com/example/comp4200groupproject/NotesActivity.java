package com.example.comp4200groupproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NotesActivity extends AppCompatActivity {
    private NotesManager notesManager;
    private NotesAdapter notesAdapter;
    private EditText editTextNote;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        notesManager = new NotesManager(this);

        // 初始化 UI 组件
        recyclerView = findViewById(R.id.recyclerViewNotes);
        editTextNote = findViewById(R.id.editTextNote);
        Button buttonAddNote = findViewById(R.id.buttonAddNote);
        Button buttonSort = findViewById(R.id.buttonSort);

        // 设置 RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadNotes();

        // 添加新笔记
        buttonAddNote.setOnClickListener(v -> {
            String note = editTextNote.getText().toString().trim();
            if (!note.isEmpty()) {
                notesManager.addNote(note);
                editTextNote.setText("");
                loadNotes();
            } else {
                Toast.makeText(this, "请输入笔记内容", Toast.LENGTH_SHORT).show();
            }
        });

        // 排序笔记
        buttonSort.setOnClickListener(v -> {
            notesManager.sortNotesAlphabetically();
            loadNotes();
        });
    }

    // 加载笔记并更新 RecyclerView
    private void loadNotes() {
        List<String> notesList = notesManager.getNotes();
        notesAdapter = new NotesAdapter(notesList, notesManager);
        recyclerView.setAdapter(notesAdapter);
    }
}
