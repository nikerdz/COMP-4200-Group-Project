package com.example.comp4200groupproject;

public class PlannerEvent {
    private String title;
    private String time;
    private String date;
    private int id;

    // Constructor with title, time, and date (id will be set later)
    public PlannerEvent(String title, String time, String date) {
        this.title = title;
        this.time = time;
        this.date = date;
    }

    // Getter methods
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    // Setter methods
    public void setTitle(String title) {
        this.title = title;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setId(int id) {
        this.id = id;
    }
}
