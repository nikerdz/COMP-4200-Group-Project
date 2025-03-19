package com.example.comp4200groupproject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Reminder {
    private String title;
    private String dueDate;

    public Reminder(String title, String dueDate) {
        this.title = title;
        this.dueDate = dueDate;
    }

    public String getTitle() { return title; }
    public String getDueDate() { return dueDate; }

    public String getCountdown() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date due = sdf.parse(dueDate);
            long diff = due.getTime() - System.currentTimeMillis();
            long daysLeft = TimeUnit.MILLISECONDS.toDays(diff);
            return daysLeft > 0 ? daysLeft + " days left" : "Expired";
        } catch (Exception e) {
            return "Invalid Date";
        }
    }
}
