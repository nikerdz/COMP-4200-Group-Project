package com.example.comp4200groupproject;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotesManager {
    private static final String PREFS_NAME = "StudyBuddyNotes";
    private static final String NOTES_KEY = "notes";
    private SharedPreferences sharedPreferences;

    public NotesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // 获取所有笔记
    public List<String> getNotes() {
        String json = sharedPreferences.getString(NOTES_KEY, "[]");
        List<String> notesList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                notesList.add(jsonArray.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return notesList;
    }

    // 添加笔记
    public void addNote(String note) {
        List<String> notesList = getNotes();
        notesList.add(note);
        saveNotes(notesList);
    }

    // 编辑笔记
    public void editNote(int index, String newNote) {
        List<String> notesList = getNotes();
        if (index >= 0 && index < notesList.size()) {
            notesList.set(index, newNote);
            saveNotes(notesList);
        }
    }

    // 删除笔记
    public void deleteNote(int index) {
        List<String> notesList = getNotes();
        if (index >= 0 && index < notesList.size()) {
            notesList.remove(index);
            saveNotes(notesList);
        }
    }

    // 按字母排序
    public void sortNotesAlphabetically() {
        List<String> notesList = getNotes();
        Collections.sort(notesList);
        saveNotes(notesList);
    }

    // 保存笔记列表到 SharedPreferences
    private void saveNotes(List<String> notesList) {
        JSONArray jsonArray = new JSONArray();
        for (String note : notesList) {
            jsonArray.put(note);
        }
        sharedPreferences.edit().putString(NOTES_KEY, jsonArray.toString()).apply();
    }
}
