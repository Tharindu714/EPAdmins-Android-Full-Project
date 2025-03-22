package com.deltacodex.epadmins.ui.Handle_Community;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.deltacodex.epadmins.Adapter.PostAdapter;
import com.deltacodex.epadmins.Adapter.SpaceItemDeco;
import com.deltacodex.epadmins.Adapter.StatusSpinnerAdapter;
import com.deltacodex.epadmins.R;
import com.deltacodex.epadmins.model.ForumPost;
import com.deltacodex.epadmins.model.StatusItem;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class Handle_Com_Fragment extends Fragment {
    private PostAdapter postAdapter;
    private ProgressBar progressBar;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_handle__com, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewPosts);
        progressBar = view.findViewById(R.id.progressBar);
        Spinner statusFilter = view.findViewById(R.id.statusFilter);

        db = FirebaseFirestore.getInstance();
        postAdapter = new PostAdapter(getContext(), new ArrayList<>(), db);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(postAdapter);
        recyclerView.addItemDecoration(new SpaceItemDeco(25));

        List<StatusItem> statusItems = new ArrayList<>();
        statusItems.add(new StatusItem("All", R.drawable.ic_pending));
        statusItems.add(new StatusItem("Approved", R.drawable.ic_approved));
        statusItems.add(new StatusItem("Blocked", R.drawable.ic_blocked));

        StatusSpinnerAdapter spinnerAdapter = new StatusSpinnerAdapter(getContext(), statusItems);
        statusFilter.setAdapter(spinnerAdapter);

        // Handle item selection
        statusFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View selectedItemView, int position, long id) {
                loadPostsByStatus(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        loadPostsByStatus(0);  // Default: Load all posts
        return view;
    }

    private void loadPostsByStatus(int filterIndex) {
        progressBar.setVisibility(View.VISIBLE);
        Query query;

        switch (filterIndex) {
            case 1: // Approved posts
                query = db.collection("CommunityForum")
                        .whereEqualTo("status", "approved")
                        .orderBy("timestamp", Query.Direction.DESCENDING);
                break;
            case 2: // Blocked posts
                query = db.collection("CommunityForum")
                        .whereEqualTo("status", "blocked")
                        .orderBy("timestamp", Query.Direction.DESCENDING);
                break;
            default: // All posts
                query = db.collection("CommunityForum")
                        .orderBy("timestamp", Query.Direction.DESCENDING);
                break;
        }

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<ForumPost> posts = new ArrayList<>();
                for (DocumentSnapshot doc : task.getResult()) {
                    ForumPost post = doc.toObject(ForumPost.class);
                    if (post != null) {
                        post.setPostId(doc.getId());
                        posts.add(post);
                    }
                }
                postAdapter.updatePosts(posts);
            } else {
                Toast.makeText(getContext(), "Error loading posts", Toast.LENGTH_SHORT).show();
            }
            progressBar.setVisibility(View.GONE);
        });
    }
}
