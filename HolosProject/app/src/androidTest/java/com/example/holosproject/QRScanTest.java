package com.example.holosproject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;


/**
 * A basic test to handle scanning a check-in QR code.
 */
@RunWith(AndroidJUnit4.class) // Added dependency for this AndroidJUnit4
@LargeTest
public class QRScanTest {

    @Rule
    public ActivityScenarioRule<TestAttendeeDashboardActivity> scenario = new
            ActivityScenarioRule<TestAttendeeDashboardActivity>(TestAttendeeDashboardActivity.class);

    /**
     * Enables test mode for the dummy attendee dashboard activity
     */
    @Before
    public void setUp() {
        TestAttendeeDashboardActivity.enableTestMode();
    }

    /**
     * A basic test to handle scanning a check-in QR code.
     */
    @Test
    public void checkInTest()  {
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
        onView(withId(R.id.textViewEventNameDiag)).check(matches(withText(eventName)));

    }

}