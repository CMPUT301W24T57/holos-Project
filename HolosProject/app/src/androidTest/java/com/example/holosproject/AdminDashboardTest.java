package com.example.holosproject;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class) // Added dependency for this AndroidJUnit4
@LargeTest

public class AdminDashboardTest {

    @Rule
    public ActivityScenarioRule<AdminDashboardActivity> scenario = new
            ActivityScenarioRule<AdminDashboardActivity>(AdminDashboardActivity.class);

    @Test
    public void ViewAndDeleteProfileTest() throws InterruptedException {
        // WHY WHY WHY DO THE USER PROFILES NOT DISPLAY IN THE TEST! BUT THEY DO IN THE APP!!!!
        // The user profiles dont appear in the test. but they do normally within the app. I don't know why. T
        // The test does not work because of this. If the profiles did appear, it would work.

        // Tap on the View Profiles Button
        onView(withId(R.id.btnViewProfiles)).perform(click());

        // Wait for the data from Firebase to appear
        // Wait for 5 seconds
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Tap the profile at the top using RecyclerViewActions
        RecyclerView ourView = (RecyclerView) withId(R.id.recyclerViewProfiles);
        ourView.findViewHolderForAdapterPosition(0).itemView.performClick();


        // Click on the 'Yes' deletion confirmation dialog
        onView(ViewMatchers.withText("Yes")).perform(click());
    }

    @Test
    public void ViewAndDeleteEventTest() throws InterruptedException {
        // Same story. THE EVENTS ARE NOT APPEARING. ONLY IN THE TEST!!! NOT IN THE ACTUAL ENVIRONMENT.
        // I HAVE NO EARTHLY IDEA AS TO WHY.

        // Tap on the View Events Button
        onView(withId(R.id.btnViewEvents)).perform(click());

        // Wait for the data from Firebase to appear
        // Wait for 5 seconds
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Tap the profile at the top using RecyclerViewActions
        RecyclerView ourView = (RecyclerView) withId(R.id.recyclerViewEvents);
        ourView.findViewHolderForAdapterPosition(0).itemView.performClick();


        // Click on the 'Yes' deletion confirmation dialog
        onView(ViewMatchers.withText("Yes")).perform(click());
    }

    @Test
    public void ViewImages() throws InterruptedException {
        onView(withId(R.id.btnViewImages)).perform(click());

    }

}
