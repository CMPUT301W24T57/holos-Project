package com.example.holosproject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.example.holosproject.AttendeeDashboardActivity.testEvent;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.google.firebase.firestore.GeoPoint;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RunWith(AndroidJUnit4.class) // Added dependency for this AndroidJUnit4
@LargeTest

/**
 *
 */

public class PromoScanTest {

    @Rule
    public ActivityScenarioRule<TestAttendeeDashboardActivity> scenario = new
            ActivityScenarioRule<TestAttendeeDashboardActivity>(TestAttendeeDashboardActivity.class);

    @Test
    // Test for "scanning" an event check-in code
    public void promoTest()  {
        List<Event> mockEvents = MockDataProvider.getMockEvents();
        Event mockEvent = mockEvents.get(0);
        UserProfile mockUser = MockDataProvider.getMockUser();

        try {
            Thread.sleep(1000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Tap on the Scan Button
        onView(withId(R.id.fabQRCode)).perform(click());

        try {
            Thread.sleep(1000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        String eventName = mockEvent.getName();
        onView(withId(R.id.textViewEventNameDiag)).check(matches(withText("EVENT NAME: " + eventName)));


    }

}