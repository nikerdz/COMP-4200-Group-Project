package com.example.comp4200groupproject;

public class PlannerEvent {
    private String title;
    private String time;

    public PlannerEvent(String title, String time) {
        this.title = title;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }
}
