package com.example.holosproject;

public class AttendeeCheckin {
    private String name;
    private int checkinCount;

    public AttendeeCheckin(String name, int checkinCount) {
        this.name = name;
        this.checkinCount = checkinCount;
    }

    public String getName() {
        return name;
    }

    public int getCheckinCount() {
        return checkinCount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCheckinCount(int checkinCount) {
        this.checkinCount = checkinCount;
    }


}
