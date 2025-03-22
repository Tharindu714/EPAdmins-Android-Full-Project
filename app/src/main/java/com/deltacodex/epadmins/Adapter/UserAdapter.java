package com.deltacodex.epadmins.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.deltacodex.epadmins.R;
import com.deltacodex.epadmins.model.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private final Context context;
    private final List<User> userList;
    private final FirebaseFirestore db;

    public UserAdapter(Context context, List<User> userList, FirebaseFirestore db) {
        this.context = context;
        this.userList = userList;
        this.db = db;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        holder.txtUserName.setText(user.getUsername());
        holder.txtUserMobileDetails.setText(user.getMobile());
        holder.txtUsergenderDetails.setText(user.getGender());
        holder.txtUserlocationDetails.setText(user.getLocation_name());

        // Change button icon based on status
        if ("blocked".equals(user.getStatus())) {
            holder.btnBlockUnblock.setImageResource(R.drawable.ic_block_user);
            holder.itemView.setBackgroundColor(Color.parseColor("#2E0018"));
        } else {
            holder.btnBlockUnblock.setImageResource(R.drawable.ic_active_user);
            holder.itemView.setBackgroundColor(Color.parseColor("#000524"));
        }

        // Click listener for block/unblock action
        holder.btnBlockUnblock.setOnClickListener(v -> {
            String newStatus = "blocked".equals(user.getStatus()) ? "active" : "blocked";
            String message = "Are you sure you want to " + (newStatus.equals("blocked") ? "block" : "unblock") + " this user?";

            // Confirmation AlertDialog
            new AlertDialog.Builder(context)
                    .setTitle("Confirm Action")
                    .setMessage(message)
                    .setPositiveButton("Yes", (dialog, which) -> {
                        user.setStatus(newStatus);

                        // Update Firestore
                        db.collection("Profile_user").document(user.getEmail())
                                .update("status", newStatus)
                                .addOnSuccessListener(aVoid -> notifyItemChanged(position))
                                .addOnFailureListener(e ->
                                        Toast.makeText(context, "Failed to update status", Toast.LENGTH_SHORT).show());
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView txtUserName, txtUserMobileDetails,txtUsergenderDetails,txtUserlocationDetails;
        ImageButton btnBlockUnblock;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            txtUserMobileDetails = itemView.findViewById(R.id.txtUserMobileDetails);
            txtUsergenderDetails = itemView.findViewById(R.id.txtUsergenderDetails);
            txtUserlocationDetails = itemView.findViewById(R.id.txtUserlocationDetails);
            btnBlockUnblock = itemView.findViewById(R.id.btnBlockUnblock);
        }
    }
}
