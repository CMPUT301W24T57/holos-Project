package com.example.holosproject;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.HashMap;

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

    private HashMap<String, String> checkIns;

    private ArrayList<GeoPoint> locations;
    private String eventId;
    private String imageUrl; // Field to store the image URL
    private int limit;

    private String qrUrl;
    private String customQRContents;

    /**
     * Constructs an event with the given attributes.
     *
     * @param name    the name of the event
     * @param date    the date of the event
     * @param time    the time of the event
     * @param address the address of the event
     * @param creator the creator of the event
     */
    public Event(String name, String date, String time, String address, String creator) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.address = address;
        this.creator = creator;
        this.eventId = "";
        this.imageUrl = null;
        this.qrUrl = null;
        this.customQRContents = null;
        this.attendees = new ArrayList<String>();
        this.limit = Integer.MAX_VALUE;
        this.checkIns = new HashMap<String, String>();
        this.locations = new ArrayList<GeoPoint>();
        this.limit = Integer.MAX_VALUE;

    }

    /**
     * Retrieves the list of attendees for this event.
     *
     * @return the list of attendees
     */
    public ArrayList<String> getAttendees() {
        return attendees;
    }

    public HashMap<String, String> getCheckIns() {
        return checkIns;
    }

    public ArrayList<GeoPoint> getLocations() {
        return locations;
    }

    public String getQrUrl() {
        return qrUrl;
    }

    public void setQrUrl(String qrUrl) {
        this.qrUrl = qrUrl;
    }

    public String getCustomQRContents() {
        return customQRContents;
    }

    public void setCustomQRContents(String customQRContents) {
        this.customQRContents = customQRContents;
    }

    public void setLocations(ArrayList<GeoPoint> locations) {
        this.locations = locations;
    }

    public void setCheckIns(HashMap<String, String> checkIns) {
        this.checkIns = checkIns;
    }

    public String getEventId() {
        return eventId;
    }

    /**
     * Sets the event ID.
     *
     * @param eventId the event ID to set
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     * Sets the list of attendees for this event.
     *
     * @param attendees the list of attendees to set
     */
    public void setAttendees(ArrayList<String> attendees) {
        this.attendees = attendees;
    }

    /**
     * Retrieves the name of the event.
     *
     * @return the name of the event
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the event.
     *
     * @param name the name of the event to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the date of the event.
     *
     * @return the date of the event
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the date of the event.
     *
     * @param date the date of the event to set
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Retrieves the time of the event.
     *
     * @return the time of the event
     */
    public String getTime() {
        return time;
    }

    /**
     * Sets the time of the event.
     *
     * @param time the time of the event to set
     */
    public void setTime(String time) {
        this.time = time;
    }
    public void setLimit(int limit){
        this.limit = limit;
    }
    public int getLimit(){
        return this.limit;
    }
    /**
     * Retrieves the address of the event.
     *
     * @return the address of the event
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the address of the event.
     *
     * @param address the address of the event to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Retrieves the creator of the event.
     *
     * @return the creator of the event
     */
    public String getCreator() {
        return creator;
    }

    /**
     * Retrieves the image URL for the poster of the event
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Sets the image URL for the poster of the event
     * @param imageUrl the URL of the poster image associated with this event
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}





