package com.deltacodex.epadmins.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.deltacodex.epadmins.R;
import com.deltacodex.epadmins.model.MediaItem;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MediaViewHolder> {
    private final Context context;
    private final List<MediaItem> mediaList;
    private String categoryType;
    private final FirebaseFirestore db;

    public MediaAdapter(Context context, List<MediaItem> mediaList, String categoryType, FirebaseFirestore db) {
        this.context = context;
        this.mediaList = mediaList;
        this.categoryType = categoryType;
        this.db = db;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    @NonNull
    @Override
    public MediaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_media, parent, false);
        return new MediaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MediaViewHolder holder, int position) {
        MediaItem item = mediaList.get(position);

        holder.mediaName.setText(item.getName());
        holder.mediaExtraInfo.setText(item.getExtraInfo());

        // Load image using Glide
        Glide.with(context).load(item.getThumbnailUrl()).into(holder.mediaThumbnail);

        // Handle Block/Unblock UI update
        if ("blocked".equals(item.getStatus())) {
            holder.btnBlockMovieUnblock.setImageResource(R.drawable.ic_block_user);
            holder.itemView.setBackgroundColor(Color.parseColor("#2E0018"));
        } else {
            holder.btnBlockMovieUnblock.setImageResource(R.drawable.ic_active_user);
            holder.itemView.setBackgroundColor(Color.parseColor("#000524"));
        }

        // Click listener for block/unblock action
        holder.btnBlockMovieUnblock.setOnClickListener(v -> {
            String newStatus = "blocked".equals(item.getStatus()) ? "approved" : "blocked";
            String message = "Are you sure you want to " + (newStatus.equals("blocked") ? "block" : "unblock") + " this media?";

            Log.d("FirestoreUpdate", "Updating collection: " + categoryType + ", Doc ID: " + item.getId() + ", New status: " + newStatus);

            new AlertDialog.Builder(context)
                    .setTitle("Confirm Action")
                    .setMessage(message)
                    .setPositiveButton("Yes", (dialog, which) -> {
                        if (item.getId() == null || item.getId().isEmpty()) {
                            Log.e("FirestoreError", "Invalid Document ID");
                            Toast.makeText(context, "Error: Invalid document ID", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        item.setStatus(newStatus);

                        db.collection(categoryType).document(item.getId())
                                .update("status", newStatus)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("FirestoreUpdate", "Successfully updated status for: " + item.getId());
                                    notifyItemChanged(position);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("FirestoreError", "Failed to update status: " + e.getMessage());
                                    Toast.makeText(context, "Failed to update status: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    public static class MediaViewHolder extends RecyclerView.ViewHolder {
        ImageView mediaThumbnail;
        TextView mediaName, mediaExtraInfo;
        ImageButton btnBlockMovieUnblock;

        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            mediaThumbnail = itemView.findViewById(R.id.mediaThumbnail);
            mediaName = itemView.findViewById(R.id.mediaName);
            mediaExtraInfo = itemView.findViewById(R.id.mediaExtraInfo);
            btnBlockMovieUnblock = itemView.findViewById(R.id.btnBlockMovieUnblock);
        }
    }
}
