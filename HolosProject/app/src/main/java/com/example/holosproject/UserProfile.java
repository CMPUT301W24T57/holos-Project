package com.example.holosproject;

public class UserProfile {
    private String name;
    private String contact;
    private String homepage;
    private String uid; // Unique ID for Firebase operations

    /**
     * Retrieves the contact details of the user.
     * @return The contact details of the user.
     */
    public String getContact() {
        return contact;
    }

    /**
     * Sets the contact details of the user.
     * @param contact The contact details to be set.
     */
    public void setContact(String contact) {
        this.contact = contact;
    }

    /**
     * Retrieves the name of the user.
     * @return The name of the user.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the user.
     * @param name The name to be set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the homepage URL of the user.
     * @return The homepage URL of the user.
     */
    public String getHomepage() {
        return homepage;
    }

    /**
     * Sets the homepage URL of the user.
     * @param homepage The homepage URL to be set.
     */
    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    /**
     * Retrieves the unique identifier of the user.
     * @return The unique identifier of the user.
     */
    public String getUid() {
        return uid;
    }

    /**
     * Sets the unique identifier of the user.
     * @param uid The unique identifier to be set.
     */
    public void setUid(String uid) {
        this.uid = uid;
    }
}