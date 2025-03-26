package com.example.comp4200groupproject;

public class Task {
    private int id;
    private String task;
    private boolean isCompleted;

    // ✅ Constructor
    public Task(int id, String task, boolean isCompleted) {
        this.id = id;
        this.task = task;
        this.isCompleted = isCompleted;
    }

    // ✅ Getters
    public int getId() {
        return id;
    }

    public String getTask() {
        return task;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    // ✅ Setters
    public void setTask(String task) {
        this.task = task;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
