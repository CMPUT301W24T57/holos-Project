package com.example.holosproject;

/**
 * The AttendeeCheckin class represents attendee check-in information,
 * including the attendee's name and the number of times they have checked in.
 */
public class AttendeeCheckin {
    private String name;
    private int checkinCount;

    /**
     * Constructs a new AttendeeCheckin object with the specified name and check-in count.
     *
     * @param name         The name of the attendee.
     * @param checkinCount The number of times the attendee has checked in.
     */
    public AttendeeCheckin(String name, int checkinCount) {
        this.name = name;
        this.checkinCount = checkinCount;
    }

    /**
     * Returns the name of the attendee.
     *
     * @return The name of the attendee.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the number of times the attendee has checked in.
     *
     * @return The number of times the attendee has checked in.
     */
    public int getCheckinCount() {
        return checkinCount;
    }

    /**
     * Sets the name of the attendee.
     *
     * @param name The name of the attendee.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the number of times the attendee has checked in.
     *
     * @param checkinCount The number of times the attendee has checked in.
     */
    public void setCheckinCount(int checkinCount) {
        this.checkinCount = checkinCount;
    }
}

