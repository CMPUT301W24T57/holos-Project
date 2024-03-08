package com.example.holosproject;

import java.sql.Time;

/**
 * FileName: Event
 * Description: This is the Event Class. Currently has a very basic implementation. Currently Dates and Names for events are stored as strings.
 **/

public class Event {
    private Integer id;
    private String name;
    private String location;
    private String time;
    private String date;

    public Event(String name, Integer id, String location, String time, String date) {
        this.name = name;
        this.date = date;
        this.id = id;
        this.location = location;
        this.time = time;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

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
