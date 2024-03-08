package com.example.holosproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * FileName: AdminViewImagesAdapter
 * Adapter used for images within the Admin view of all images. Includes logic for the click listeners.
 *
 * layout files associated with this: admin_view_images, image_border.xml (the borders and background around each image), admin_image_list_item.xml (each individual item)
 **/

public class AdminViewImagesAdapter extends RecyclerView.Adapter<AdminViewImagesAdapter.ViewHolder> {

    private List<Integer> imagesList;
    private Context context; // Define context to use in dialog

    /**
     * Constructs an AdminViewImagesAdapter with the specified list of image resources.
     *
     * @param imagesList The list of image resources to be displayed.
     */
    public AdminViewImagesAdapter(List<Integer> imagesList) {
        this.imagesList = imagesList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Use the parent context to inflate the layout
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.admin_image_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Bind the image resource to the ImageView
        holder.image.setImageResource(imagesList.get(position));
        // Set the click listener for the ImageView
        holder.image.setOnClickListener(view -> showDeleteConfirmation());
    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    /**
     * ViewHolder class for holding ImageView references.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;

        /**
         * Constructs a ViewHolder with the given View.
         *
         * @param itemView The view containing the ImageView.
         */
        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image); // Make sure this is the correct ID
        }
    }

    /**
     * Displays a dialog for confirming image deletion.
     */
    private void showDeleteConfirmation() {
        // Use the context passed to the adapter to create the dialog
        new AlertDialog.Builder(context)
                .setTitle("Delete Image")
                .setMessage("Are you sure you want to delete this image?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    // Placeholder for delete action
                })
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
