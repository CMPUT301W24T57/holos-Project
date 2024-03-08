package com.example.holosproject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AdminDashboardTest {

    @Rule
    public ActivityTestRule<AdminDashboardActivity> activityRule =
            new ActivityTestRule<>(AdminDashboardActivity.class);

    @Before
    public void setUp() {
        // The activity is started automatically by the ActivityTestRule.
        // Additional setup if required.
    }

    @Test
    public void testDeleteProfileFunctionality() {
        // Click on the "View Profiles" button
        onView(withId(R.id.btnViewProfiles)).perform(click());

        // Use Thread.sleep to wait for Firestore to fetch the profiles
        // Note: This is not a best practice and should ideally be replaced with an IdlingResource
        try {
            Thread.sleep(5000); // Wait for 5 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Click on the first profile in the list
        onView(withId(R.id.recyclerViewProfiles)) // Assuming you have a RecyclerView with this ID
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // Click "Yes" on the dialog to confirm deletion
        onView(withText("Yes")).perform(click());

        // You can add a check here to verify that the profile has been deleted
        // This would probably require observing some change in the UI or the data source
    }

    @After
    public void tearDown() {
        // Perform any necessary cleanup after tests are complete, such as logging out the admin
    }
}
