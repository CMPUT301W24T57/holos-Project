package com.example.holosproject;

import java.util.ArrayList;
import java.util.List;

/**
 * FileName: MockDataProvider
 * Description: Provides mock data to relevant views when running App Tests. This class is used entirely for tests.
 **/


public class MockDataProvider {

    /**
     * Creates a list of mock user profiles.
     * @return A static list of user profiles.
     */
    public static List<UserProfile> getMockProfiles() {
        List<UserProfile> mockProfiles = new ArrayList<>();

        UserProfile profile1 = new UserProfile();
        profile1.setName("John Doe");
        profile1.setContact("johndoe@example.com");
        profile1.setHomepage("http://johndoe.com");
        profile1.setUid("uid1");
        mockProfiles.add(profile1);

        UserProfile profile2 = new UserProfile();
        profile2.setName("Bob Evans");
        profile2.setContact("bobevans@bing.com");
        profile2.setHomepage("http://bobevans.com");
        profile2.setUid("uid2");
        mockProfiles.add(profile2);


        return mockProfiles;
    }


    /**
     * Retrieves a list of mock events for testing purposes.
     *
     * @return A list of mock Event objects.
     */

    public static List<Event> getMockEvents() {
        List<Event> mockEvents = new ArrayList<>();

        Event event1 = new Event("Technology Conference", "May 17, 2024", "4:00PM", "New York City"
        , "Josh Allen");
        event1.setEventId("event1");
        event1.setImageUrl("https://placebear.com/500/500");

        Event event2 = new Event("Pizza Time", "April 28, 2024", "1:00PM", "Edmonton"
                , "Peter Parker");
        event2.setEventId("event2");

        Event event3 = new Event("10 KM Marathon", "August 3rd, 2024", "10:00 AM", "Downton Calgary"
                , "Runner McGarth");
        event3.setEventId("event3");

        mockEvents.add(event1);
        mockEvents.add(event2);
        mockEvents.add(event3);

        return mockEvents;
    }
    /**
     * Retrieves a list of mock image URLs for testing purposes.
     *
     * @return A list of mock image URLs.
     */

    public static List<String> getMockImages() {
        List<String> mockImages = new ArrayList<>();
        // Add mock URLs
        mockImages.add("https://placebear.com/500/500");
        mockImages.add("https://placebear.com/400/400");
        mockImages.add("https://placebear.com/600/600");
        mockImages.add("https://placebear.com/700/700");

        return mockImages;
    }

    /**
     * Retrieves a mock user profile for testing purposes.
     *
     * @return A mock user profile.
     */

    public static UserProfile getMockUser() {
        UserProfile mockUser = new UserProfile();
        mockUser.setUid("test12345");
        mockUser.setName("Test User");
        return mockUser;
    }


}
