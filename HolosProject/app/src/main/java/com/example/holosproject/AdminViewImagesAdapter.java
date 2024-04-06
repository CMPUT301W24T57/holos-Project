package com.example.holosproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

/**
 * FileName: AdminViewImagesAdapter
 * Adapter used for images within the Admin view of all images. Includes logic for the click listeners.
 *
 * layout files associated with this: admin_view_images, image_border.xml (the borders and background around each image), admin_image_list_item.xml (each individual item)
 **/

public class AdminViewImagesAdapter extends RecyclerView.Adapter<AdminViewImagesAdapter.ViewHolder> {

    private final List<String> imageUrls;
    private final Context context;
    // Static variable to control test mode
    public static boolean isTestMode = false;

    public AdminViewImagesAdapter(List<String> imageUrls, Context context) {
        this.imageUrls = imageUrls;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_view_images_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(context).load(imageUrls.get(position)).into(holder.imageViewItem);
        holder.imageViewItem.setOnClickListener(v -> showDeleteConfirmation(position));
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewItem;

        public ViewHolder(View view) {
            super(view);
            imageViewItem = view.findViewById(R.id.imageViewItem);
        }
    }

    /**
     * Displays a dialog for confirming image deletion.
     */
    private void showDeleteConfirmation(int position) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Image")
                .setMessage("Are you sure you want to delete this image?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> deleteImage(position))
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * Deletes the selected image from either profileImages or eventImages on firebase.
     * @param position the position of the selected image in the recycler view
     */
    private void deleteImage(int position) {

        // making sure we are not deleting the default profile image
        String defaultProfilePicture = "https://firebasestorage.googleapis.com/v0/b/cmput-301-holosproject.appspot.com/o/profileImages%2Fdefault.png?alt=media&token=c8fccd35-cabe-4274-9f9a-f4c0607b2e4c";
        if (imageUrls.get(position).equals(defaultProfilePicture)) {
            Toast.makeText(context, "Error: Do not try to delete default profile image!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isTestMode) {
            imageUrls.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, imageUrls.size());

        } else {
            String url = imageUrls.get(position);
            StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(url);

            // Delete the image from Firebase Storage
            ref.delete().addOnSuccessListener(aVoid -> {
                if (url.contains("profileImages")) {
                    updateFirestoreDocument("userProfiles", "profileImageUrl", url);
                } else if (url.contains("eventImages")) {
                    updateFirestoreDocument("events", "imageUrl", url);
                }
                // Remove the URL from the adapter's data set and notify the item removed
                imageUrls.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, imageUrls.size());
            }).addOnFailureListener(e -> {
                Toast.makeText(context, "Failed to delete image.", Toast.LENGTH_SHORT).show();
            });
        }
    }

    /**
     * Updates the respective document tied to the image (event or profile), and sets the imageUrl to be blank
     * @param collection the document we will be searching
     * @param field the field in the document we will be searching
     * @param imageUrl the imageUrl that we will be setting to empty
     */
    private void updateFirestoreDocument(String collection, String field, String imageUrl) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(collection)
                .whereEqualTo(field, imageUrl)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        documentSnapshot.getReference().update(field, "")
                                .addOnSuccessListener(aVoid -> Toast.makeText(context, "Document updated", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(context, "Failed to update document", Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to find document", Toast.LENGTH_SHORT).show());
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
