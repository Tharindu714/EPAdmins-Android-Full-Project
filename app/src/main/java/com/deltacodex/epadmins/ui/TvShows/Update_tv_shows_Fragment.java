package com.deltacodex.epadmins.ui.TvShows;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deltacodex.epadmins.R;
import com.deltacodex.epadmins.model.StatusBarUtils;
import com.deltacodex.epadmins.model.TvShowModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Update_tv_shows_Fragment extends Fragment {

    private RecyclerView recyclerView;
    private TvShowAdapter adapter;
    private List<TvShowModel> tvShowList;
    private FirebaseFirestore db;
    private ProgressBar progressBar;

    public Update_tv_shows_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_update_tv_shows, container, false);
        if (getActivity() != null) {
            StatusBarUtils.applyGradientStatusBar(getActivity());  // Pass the Activity context
        }
        db = FirebaseFirestore.getInstance();

        recyclerView = rootView.findViewById(R.id.recycler_view_tv_shows);
        progressBar = rootView.findViewById(R.id.progress_bar);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        tvShowList = new ArrayList<>();
        adapter = new TvShowAdapter(getContext(), tvShowList, this);
        recyclerView.setAdapter(adapter);
        adapter.attachSwipeToUpdate(recyclerView);
        showSwipeInstruction();
        fetchTvShows();

        return rootView;
    }

    private void showSwipeInstruction() {
        // Check if the view is not null
        View view = getView();
        if (view == null) {
            return; // Return early if the view is not available
        }

        TextView instructionView = view.findViewById(R.id.swipe_instruction);
        if (instructionView == null) {
            return; // Return early if the view is not found
        }

        instructionView.setVisibility(View.VISIBLE);

        // Create fade-in and fade-out animations
        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(800);

        AlphaAnimation fadeOut = new AlphaAnimation(1f, 0f);
        fadeOut.setDuration(800);
        fadeOut.setStartOffset(3000); // Display for 3 seconds before fading out

        // Start animations
        instructionView.startAnimation(fadeIn);
        instructionView.startAnimation(fadeOut);

        // Hide it after animation ends
        new Handler().postDelayed(() -> instructionView.setVisibility(View.GONE), 4000);
    }

    // Call this method in `onViewCreated()` to ensure view has been created
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showSwipeInstruction(); // Call it after the view is created
    }


    private void fetchTvShows() {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("tv_shows") // Make sure "tv_shows" is your collection name
                .addSnapshotListener((value, error) -> {
                    progressBar.setVisibility(View.GONE);

                    if (error != null) {
                        Log.e("Firebase", "Error listening for updates.", error);
                        return;
                    }

                    if (value != null) {
                        tvShowList.clear(); // Clear old data to avoid duplicates
                        for (DocumentSnapshot document : value.getDocuments()) {
                            TvShowModel tvShow = document.toObject(TvShowModel.class);
                            tvShowList.add(tvShow);
                        }
                        adapter.notifyDataSetChanged(); // Refresh RecyclerView
                    }
                });
    }


    public void showUpdateDialog(TvShowModel tvShow) {
        UpdateTvShowDialog dialog = UpdateTvShowDialog.newInstance(tvShow);
        dialog.show(getParentFragmentManager(), "updateTvShowDialog");
    }
}

