package com.example.comp4200groupproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "study_buddy.db";
    public static final int DATABASE_VERSION = 5;  // Incremented version for new table

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE reminders (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, due_date TEXT)");
        db.execSQL("CREATE TABLE notes (id INTEGER PRIMARY KEY AUTOINCREMENT, content TEXT)");
        db.execSQL("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, education TEXT)");
        db.execSQL("CREATE TABLE todo (id INTEGER PRIMARY KEY AUTOINCREMENT, task TEXT, is_completed INTEGER DEFAULT 0)");

        // Create the new events table for PlannerActivity
        db.execSQL("CREATE TABLE IF NOT EXISTS events (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "event_name TEXT, " +
                "event_time TEXT, " +
                "event_date TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop old tables if they exist
        db.execSQL("DROP TABLE IF EXISTS reminders");
        db.execSQL("DROP TABLE IF EXISTS notes");
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS todo");
        db.execSQL("DROP TABLE IF EXISTS events"); // Drop the events table if it exists (for upgrades)

        // Re-create all tables
        if (oldVersion < 5) {
            // Add the events table for PlannerActivity
            db.execSQL("CREATE TABLE IF NOT EXISTS events (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "event_name TEXT, " +
                    "event_time TEXT, " +
                    "event_date TEXT)");
        }

        // Create new tables again
        onCreate(db);
    }

    // INSERT a new task
    public long addTask(String task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("task", task);
        values.put("is_completed", 0);  // Default to incomplete

        long id = db.insert("todo", null, values);
        db.close();
        return id;
    }

    // RETRIEVE all tasks
    public List<Task> getAllTasks() {
        List<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM todo", null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String task = cursor.getString(cursor.getColumnIndexOrThrow("task"));
            boolean isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow("is_completed")) == 1;

            taskList.add(new Task(id, task, isCompleted));
        }

        cursor.close();
        db.close();
        return taskList;
    }

    // UPDATE task content
    public void updateTask(int id, String newTask) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("task", newTask);
        db.update("todo", values, "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // TOGGLE task completion status
    public void updateTaskCompletionStatus(String taskId, boolean isCompleted) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_completed", isCompleted ? 1 : 0);

        db.update("todo", values, "id = ?", new String[]{taskId});
        db.close();
    }


    // DELETE a task
    public void deleteTask(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("todo", "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Insert user data
    public long insertUser(String name, String education) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("education", education);

        long id = db.insert("users", null, values);
        db.close();
        return id;
    }

    // Retrieve user data
    public User getUser() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users LIMIT 1", null);
        User user = null;

        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String education = cursor.getString(cursor.getColumnIndexOrThrow("education"));
            user = new User(name, education);
        }

        cursor.close();
        db.close();
        return user;
    }

    // Reminder insert method
    public long insertReminder(String title, String dueDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("due_date", dueDate);

        long id = db.insert("reminders", null, values);
        db.close();
        return id;
    }

    public int updateReminder(Reminder reminder) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Add updated values
        values.put("title", reminder.getTitle());
        values.put("date", reminder.getDate());

        // Update the reminder by ID
        int rowsAffected = db.update(
                "reminders",                // Table name
                values,                     // Updated values
                "id = ?",                   // WHERE clause
                new String[]{String.valueOf(reminder.getId())}  // Arguments
        );

        db.close();
        return rowsAffected;  // Return the number of rows updated
    }


    // Update user data
    public void updateUser(int id, String name, String education) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("education", education);

        db.update("users", values, "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Delete user data
    public void deleteUser(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("users", "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Modify the insertEvent method to accept PlannerEvent
    public long insertEvent(PlannerEvent event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("event_name", event.getTitle());
        values.put("event_time", event.getTime());
        values.put("event_date", event.getDate());

        // Insert the event into the database and return the row ID
        long result = db.insert("events", null, values);
        db.close();
        return result;
    }


    // Method to delete an event by ID
    public void deleteEvent(int eventId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = "id = ?";
        String[] whereArgs = {String.valueOf(eventId)};

        // Delete the event
        db.delete("events", whereClause, whereArgs);
        db.close();
    }

    // Method to update an event's details
    public void updateEvent(PlannerEvent plannerEvent) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Extract values from the PlannerEvent object
        values.put("event_name", plannerEvent.getTitle());
        values.put("event_time", plannerEvent.getTime());
        values.put("event_date", plannerEvent.getDate());

        String whereClause = "id = ?";
        String[] whereArgs = {String.valueOf(plannerEvent.getId())};

        // Update the event
        db.update("events", values, whereClause, whereArgs);
        db.close();
    }


    public List<PlannerEvent> getAllEvents(String date) {
        List<PlannerEvent> eventList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {"event_name", "event_time", "event_date"};
        String selection = "event_date = ?";
        String[] selectionArgs = {date};

        Cursor cursor = db.query("events", columns, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int eventNameIndex = cursor.getColumnIndex("event_name");
                int eventTimeIndex = cursor.getColumnIndex("event_time");
                int eventDateIndex = cursor.getColumnIndex("event_date");

                // Check if the column index is valid (>= 0) before retrieving the value
                String eventName = (eventNameIndex >= 0) ? cursor.getString(eventNameIndex) : null;
                String eventTime = (eventTimeIndex >= 0) ? cursor.getString(eventTimeIndex) : null;
                String eventDate = (eventDateIndex >= 0) ? cursor.getString(eventDateIndex) : null;

                eventList.add(new PlannerEvent(eventName, eventTime, eventDate));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return eventList;
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

        // Include the ID in the query
        Cursor cursor = db.rawQuery("SELECT id, title, due_date FROM reminders", null);

        while (cursor.moveToNext()) {
            // Retrieve the ID along with the title and due_date
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String dueDate = cursor.getString(cursor.getColumnIndexOrThrow("due_date"));

            // Create Reminder object with the ID
            reminders.add(new Reminder(id, title, dueDate));
        }

        cursor.close();
        db.close();
        return reminders;
    }



    public void deleteReminder(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Delete by ID to avoid accidental duplicates
        db.delete("reminders", "id = ?", new String[]{String.valueOf(id)});

        db.close();
    }


    // Notes Section (already present)

    public long addNote(String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("content", content);

        long id = db.insert("notes", null, values);

        return id;  // Keep the connection open
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