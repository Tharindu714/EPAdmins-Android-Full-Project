package com.deltacodex.epadmins.ui.BlockProduct;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import com.deltacodex.epadmins.Adapter.MediaAdapter;
import com.deltacodex.epadmins.Adapter.SpinnerAdapter;
import com.deltacodex.epadmins.R;
import com.deltacodex.epadmins.model.MediaItem;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
public class BlockProductFragment extends Fragment {
    private MediaAdapter mediaAdapter;
    private List<MediaItem> mediaList, filteredList;
    private FirebaseFirestore db;
    private String currentCollection = "tv_shows";  // Default category

    private RecyclerView recyclerView;
    private Spinner spinner;
    private EditText searchBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_block_product, container, false);

        spinner = view.findViewById(R.id.spinnerCategory);
        searchBar = view.findViewById(R.id.searchBar);
        recyclerView = view.findViewById(R.id.recyclerViewMedia);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));

        mediaList = new ArrayList<>();
        filteredList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        mediaAdapter = new MediaAdapter(getContext(), filteredList, currentCollection, db);
        recyclerView.setAdapter(mediaAdapter);

        // Spinner Adapter Setup
        spinner.setAdapter(new SpinnerAdapter(getContext()));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentCollection = getCategoryByPosition(position);
                Log.d("SpinnerSelection", "Category selected: " + currentCollection);
                loadMedia(currentCollection);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Search Listener
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMedia(s.toString().trim());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadMedia(String collection) {
        db.collection(collection).get().addOnSuccessListener(query -> {
            mediaList.clear();
            filteredList.clear();

            for (DocumentSnapshot doc : query) {
                String id = doc.getId();
                String name = doc.getString(collection.equals("movies") ? "Movie_name" : "name");
                String thumbnail = doc.getString(collection.equals("movies") ? "Movie_thumbnailUrl" : "thumbnailUrl");
                String extraInfo = getExtraInfo(doc, collection);
                String status = doc.getString("status");

                Log.d("LoadMedia", "Fetched item: " + name + ", Extra Info: " + extraInfo + ", Status: " + status);
                mediaList.add(new MediaItem(id, thumbnail, name, extraInfo, status));
            }

            filteredList.addAll(mediaList);

            // Update adapter with the correct category
            mediaAdapter.setCategoryType(collection);
            mediaAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e ->
                Log.e("FirestoreError", "Error fetching media: " + e.getMessage())
        );
    }

    private String getExtraInfo(DocumentSnapshot doc, String collection) {
        switch (collection) {
            case "games":
                String releasedDate = doc.getString("Released_Date");
                return releasedDate != null ? "Released on: " + releasedDate : "No data";
            case "movies":
                String userLove = doc.getString("Movie_userLove");
                return userLove != null ? userLove + "% users like this" : "No data";
            case "tv_shows":
                userLove = doc.getString("userLove");
                return userLove != null ? userLove + "% users like this" : "No data";
        }
        return "No data";
    }

    private String getCategoryByPosition(int position) {
        switch (position) {
            case 0: return "tv_shows";
            case 1: return "movies";
            default: return "games";
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filterMedia(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(mediaList);
        } else {
            for (MediaItem item : mediaList) {
                if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }
        mediaAdapter.notifyDataSetChanged();
    }
}

