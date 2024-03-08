package com.example.holosproject;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

/**
 * FileName: AdminViewImagesActivity
 * Description: The activity used to display all images uploaded to the app, as well as ability to delete them.
 * AdminViewImagesActivity is associated with admin_view_images.xml, image_border.xml (the borders and background around each image), and admin_image_list_item.xml (each individual item).
 **/
public class AdminViewImagesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdminViewImagesAdapter imagesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_view_images);

        recyclerView = findViewById(R.id.imagesRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3)); // 3 columns in grid
        imagesAdapter = new AdminViewImagesAdapter(getPlaceholderImages());
        recyclerView.setAdapter(imagesAdapter);
    }

    /**
     * Provides a list of placeholder images.
     *
     * @return A list of placeholder image resource IDs.
     */
    private List<Integer> getPlaceholderImages() {
        // This would actually be URLs or IDs of images from Firebase,
        // but for now, just return a list of the same placeholder image
        return Collections.nCopies(100, R.drawable.ic_launcher_foreground);
    }

    // TODO: Implement Images
    // TODO: After images are implemented, integrate with Firebase
}
