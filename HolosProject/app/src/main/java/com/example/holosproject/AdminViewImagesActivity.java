package com.example.holosproject;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

/**
 * FileName: AdminViewImagesActivity
 * Description: The activity used to display all images uploaded to the app, as well as ability to delete them.
 * AdminViewImagesActivity is associated with admin_view_images.xml, image_border.xml (the borders and background around each image), and admin_image_list_item.xml (each individual item).
 **/
public class AdminViewImagesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminViewImagesAdapter imagesAdapter;
    private final List<String> imageUrls = new ArrayList<>();
    // Static variable to control test mode
    public static boolean isTestMode = false;


    /**
     * Called when the activity is first created.
     * It sets up the layout, initializes the RecyclerView, and populates it with images.
     * It also sets a click listener for the back button to finish the activity and return to the previous one.
     *
     * @param savedInstanceState A Bundle object containing the activity's previously saved state, if any.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_view_images);

        // Back button
        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            // Finish this activity and go back to the previous one in the stack
            finish();
        });

        recyclerView = findViewById(R.id.imagesRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // can change num of columns. 2 looks best imo
        // If we are in test mode, set the recyclerview to contain our mock data.
        if (isTestMode) {
            List<String> mockData = MockDataProvider.getMockImages();
            imagesAdapter = new AdminViewImagesAdapter(mockData, this);
            recyclerView.setAdapter(imagesAdapter);

        } else {
            imagesAdapter = new AdminViewImagesAdapter(imageUrls, this);
            recyclerView.setAdapter(imagesAdapter);
            fetchImages();
        }



    }

    /**
     * Fetches image URLs from Firebase Storage.
     * This method fetches image URLs for both profile images and event images.
     */
    private void fetchImages() {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        fetchImageUrls(storageRef.child("profileImages"));
        fetchImageUrls(storageRef.child("eventImages"));
    }

    /**
     * Fetches image URLs from the specified folder in Firebase Storage.
     *
     * @param imageFolderRef The reference to the folder containing the images.
     */
    private void fetchImageUrls(StorageReference imageFolderRef) {
        imageFolderRef.listAll()
                .addOnSuccessListener(listResult -> {
                    for (StorageReference itemRef : listResult.getItems()) {
                        itemRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            imageUrls.add(uri.toString());
                            imagesAdapter.notifyItemInserted(imageUrls.size() - 1);
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle any errors here
                });
    }

    /**
     * Enables test mode, used when running app tests.
     */
    // Static method to enable test mode
    public static void enableTestMode() {
        isTestMode = true;
    }
    /**
     * Disables test mode, used when running app tests.
     */
    public static void disableTestMode() {
        isTestMode = false;
    }

}
