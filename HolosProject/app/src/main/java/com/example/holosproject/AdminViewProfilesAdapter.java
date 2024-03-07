package com.example.holosproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdminViewProfilesAdapter extends RecyclerView.Adapter<AdminViewProfilesAdapter.ViewHolder> {

    private List<UserProfile> profiles;
    private LayoutInflater inflater;
    private ItemClickListener clickListener;

    // Data is passed into the constructor
    AdminViewProfilesAdapter(Context context, List<UserProfile> profiles) {
        this.inflater = LayoutInflater.from(context);
        this.profiles = profiles;
    }

    // Inflates the cell layout from XML when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.admin_view_profiles_list_item, parent, false);
        return new ViewHolder(view);
    }

    // Binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserProfile profile = profiles.get(position);
        holder.usernameTextView.setText(profile.getUsername());
        holder.contactTextView.setText(profile.getContact());
        holder.homepageTextView.setText(profile.getHomepage());
        // Use a placeholder image for now
        holder.imageView.setImageResource(R.drawable.ic_launcher_foreground);
    }

    // Total number of cells
    @Override
    public int getItemCount() {
        return profiles.size();
    }

    // Stores and recycles views as they are scrolled off screen
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

    // Parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    // Method that allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }
}
