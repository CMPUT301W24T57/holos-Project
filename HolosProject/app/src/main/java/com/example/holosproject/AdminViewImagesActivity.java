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
    private List<String> imageUrls = new ArrayList<>();

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
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // Or any number of columns you want
        imagesAdapter = new AdminViewImagesAdapter(imageUrls, this);
        recyclerView.setAdapter(imagesAdapter);

        fetchImages();

    }

    private void fetchImages() {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        fetchImageUrls(storageRef.child("profileImages"));
        fetchImageUrls(storageRef.child("eventImages"));
    }

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

}
