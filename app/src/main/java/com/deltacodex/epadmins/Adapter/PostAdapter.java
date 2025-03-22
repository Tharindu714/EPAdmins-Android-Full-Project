package com.deltacodex.epadmins.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.deltacodex.epadmins.R;
import com.deltacodex.epadmins.model.ForumPost;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private final Context context;
    private final FirebaseFirestore db;
    private List<ForumPost> forumPostList;

    public PostAdapter(Context context, List<ForumPost> forumPostList, FirebaseFirestore db) {
        this.context = context;
        this.forumPostList = forumPostList;
        this.db = db;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post_com, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        ForumPost post = forumPostList.get(position);
        // Format and display the timestamp
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
        String formattedDate = (post.getTimestamp() != null)
                ? sdf.format(post.getTimestamp().toDate())
                : "Unknown Date";

        // Set the formatted date to the TextView
        // Bind post data to the views
        holder.postTitle.setText(post.getPostTitle());
        holder.postContent.setText(post.getPostContent());
        holder.postDate.setText(formattedDate);

        // Set the background color based on the status
        if ("approved".equals(post.getStatus())) {
            holder.itemView.setBackgroundColor(Color.parseColor("#000524"));  // Approved posts (blueish color)
            holder.postDate.setTextColor(Color.parseColor("#00BFA5"));
            holder.statusIcon.setImageResource(R.drawable.ic_approved); // Change to your approved icon
        } else if ("blocked".equals(post.getStatus())) {
            holder.itemView.setBackgroundColor(Color.parseColor("#2E0018"));  // Blocked posts (reddish color)
            holder.postDate.setTextColor(Color.parseColor("#EF8484"));
            holder.statusIcon.setImageResource(R.drawable.ic_blocked); // Change to your blocked icon
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);  // Default background
            holder.statusIcon.setImageResource(R.drawable.ic_pending); // Default pending icon
        }

        holder.itemView.setOnClickListener(v -> {
            if (post.getPostId() == null) {
                Toast.makeText(context, "Post ID is missing", Toast.LENGTH_SHORT).show();
                return;
            }

            String newStatus = "approved".equals(post.getStatus()) ? "blocked" : "approved";
            String message = "Are you sure you want to " + (newStatus.equals("blocked") ? "block" : "approve") + " this post?";

            new AlertDialog.Builder(context)
                    .setTitle("Confirm Action")
                    .setMessage(message)
                    .setPositiveButton("Yes", (dialog, which) -> {
                        post.setStatus(newStatus);
                        db.collection("CommunityForum")
                                .document(post.getPostId())
                                .update("status", newStatus)
                                .addOnSuccessListener(aVoid -> {
                                    notifyItemChanged(position);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Failed to update status", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss()) // Dismiss dialog on No
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return forumPostList.size();
    }

    // Method to update posts in the adapter
    public void updatePosts(List<ForumPost> posts) {
        this.forumPostList = posts;
        notifyDataSetChanged();
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView postTitle, postContent, postDate;
        ImageView statusIcon; // Added ImageView for status icons

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postTitle = itemView.findViewById(R.id.postTitle);
            postContent = itemView.findViewById(R.id.postContent);
            postDate = itemView.findViewById(R.id.postDate);
            statusIcon = itemView.findViewById(R.id.statusIcon); // Ensure this is in your XML layout
        }
    }
}


