package com.example.comp4200groupproject;

public class PlannerEvent {
    private String title;
    private String time;
    private String date;

    public PlannerEvent(String title, String time, String date) {
        this.title = title;
        this.time = time;
        this.date = date;
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
}
