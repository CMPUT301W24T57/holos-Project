package com.example.holosproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;


/**
 * FileName: AdminViewProfilesAdapter

 * Adapter for the recyclerview used to view all user profiles in the admin panel
 **/

public class AdminViewProfilesAdapter extends RecyclerView.Adapter<AdminViewProfilesAdapter.ViewHolder> {

    private final List<UserProfile> profiles;
    private final LayoutInflater inflater;
    private ItemClickListener clickListener;
    private final Context context;

    /**
     * Constructor for the AdminViewProfilesAdapter.
     *
     * @param context  The context of the calling activity or fragment.
     * @param profiles List of user profiles to be displayed.
     */
    AdminViewProfilesAdapter(Context context, List<UserProfile> profiles) {
        this.inflater = LayoutInflater.from(context);
        this.profiles = profiles;
        this.context = context;
    }

    /**
     * Inflates the cell layout from XML when needed.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.admin_view_profiles_list_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds the data to the TextView and ImageView in each cell.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserProfile profile = profiles.get(position);
        holder.usernameTextView.setText(profile.getName());
        holder.contactTextView.setText(profile.getContact());
        holder.homepageTextView.setText(profile.getHomepage());

        // Load profile image if URL is available, otherwise load default
        if (profile.getProfileImageUrl() != null && !profile.getProfileImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(profile.getProfileImageUrl())
                    //.circleCrop() // Makes the image circular (i think it looks better disabled)
                    .placeholder(R.drawable.ic_launcher_foreground) // Placeholder image
                    .into(holder.imageView);
        } else {
            Glide.with(context)
                    .load(R.drawable.ic_launcher_foreground) // Load default image if no URL
                    //.circleCrop() // Makes the image circular (i think it looks better disabled)
                    .into(holder.imageView);
        }
    }

    /**
     * Returns the total number of cells in the data set held by the adapter.
     *
     * @return The total number of cells in the data set.
     */
    @Override
    public int getItemCount() {
        return profiles.size();
    }

    /**
     * Stores and recycles views as they are scrolled off screen.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView usernameTextView;
        TextView contactTextView;
        TextView homepageTextView;
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            contactTextView = itemView.findViewById(R.id.contactInfoTextView);
            homepageTextView = itemView.findViewById(R.id.homepageTextView);
            imageView = itemView.findViewById(R.id.profileImageView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick(view, getAdapterPosition());
        }
    }

    /**
     * Interface for defining click listener for RecyclerView items.
     */
    public interface ItemClickListener {
        /**
         * Called when a view has been clicked.
         *
         * @param view     The view that was clicked.
         * @param position The position of the view in the RecyclerView.
         */
        void onItemClick(View view, int position);
    }

    /**
     * Sets the click listener for RecyclerView items.
     *
     * @param itemClickListener The click listener to be set.
     */
    void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }
}
