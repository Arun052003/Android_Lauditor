package com.digicoffer.lauditor.Calendar.Models;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Day {
    private String date;
    private List<Event> events;
    private boolean hasEvents;
    private boolean isToday;
    private Calendar calendar;

    public Day(String date) {
        this.date = date;
        this.events = new ArrayList<>();
        this.hasEvents = false;
        this.isToday = false;

        // Initialize the Calendar instance and set the date
        calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {
            calendar.setTime(dateFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getDate() {
        return date;
    }

    public void addEvent(Event event) {
        events.add(event);
    }

    public List<Event> getEvents() {
        return events;
    }

    public boolean hasEvents() {
        return hasEvents;
    }

    public void setHasEvents(boolean hasEvents) {
        this.hasEvents = hasEvents;
    }

    public boolean isToday() {
        Calendar today = Calendar.getInstance();
        return calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                calendar.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH);
    }

    public String getEventsAsString() {
        if (events.isEmpty()) {
            return "No events";
        } else {
            StringBuilder builder = new StringBuilder();
            for (Event event : events) {
                builder.append(event.getName()).append(": ").append(event.getTime()).append("\n");
            }
            return builder.toString();
        }
    }
}