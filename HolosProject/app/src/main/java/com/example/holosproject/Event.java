package com.example.holosproject;

import java.util.ArrayList;

/**
 * FileName: Event
 * Description: This is the Event Class. It represents an event with various attributes.
 **/

public class Event {
    private String name;
    private String date;
    private String time;
    private String address;
    private final String creator;
    private ArrayList<String> attendees;
    private String eventId;

    public Event(String name, String date, String time, String address, String creator) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.address = address;
        this.creator = creator;
        this.eventId = "";
        this.attendees = new ArrayList<String>();

    }

    // Getters and setters for the event attributes
    public ArrayList<String> getAttendees() {
        return attendees;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public void setAttendees(ArrayList<String> attendees) {
        this.attendees = attendees;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreator() {
        return creator;
    }
}
