package com.example.holosproject;

/**
 * FileName: Event
 * Description: This is the Event Class. Currently has a very basic implementation. Currently Dates and Names for events are stored as strings.
 **/

public class Event {
    private String name;
    private String date;

    public Event(String name, String date) {
        this.name = name;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setDate(String date){
        this.date = date;
    }
}
