package com.example.comp4200groupproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, "reminders.db", null, 2); // Ensure version is up to date
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE reminders (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, due_date TEXT)");
        db.execSQL("CREATE TABLE notes (id INTEGER PRIMARY KEY AUTOINCREMENT, content TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS reminders");
        db.execSQL("DROP TABLE IF EXISTS notes");
        onCreate(db);
    }

    // ✅ Reminder insert method
    public long insertReminder(String title, String dueDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("due_date", dueDate);

        long id = db.insert("reminders", null, values);
        db.close();
        return id;
    }

    // Optional: existing Reminder method (if still used elsewhere)
    public void addReminder(String title, String dueDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("due_date", dueDate);

        db.insert("reminders", null, values);
        db.close();
    }

    public List<Reminder> getAllReminders() {
        List<Reminder> reminders = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM reminders", null);

        while (cursor.moveToNext()) {
            reminders.add(new Reminder(
                    cursor.getString(cursor.getColumnIndexOrThrow("title")),
                    cursor.getString(cursor.getColumnIndexOrThrow("due_date"))
            ));
        }

        cursor.close();
        db.close();
        return reminders;
    }

    public void deleteReminder(String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("reminders", "title = ?", new String[]{title});
        db.close();
    }

    // ✅ Notes Section (already present)

    public long addNote(String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("content", content);
        long id = db.insert("notes", null, values);
        db.close();
        return id;
    }

    public List<Note> getAllNotes() {
        List<Note> notesList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM notes", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String content = cursor.getString(cursor.getColumnIndexOrThrow("content"));
            notesList.add(new Note(id, content));
        }

        cursor.close();
        db.close();
        return notesList;
    }

    public void updateNote(int id, String newContent) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("content", newContent);
        db.update("notes", values, "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteNote(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("notes", "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }
}
