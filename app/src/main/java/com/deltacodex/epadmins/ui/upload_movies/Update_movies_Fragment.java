package com.deltacodex.epadmins.ui.upload_movies;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.deltacodex.epadmins.R;
import com.deltacodex.epadmins.model.MovieModel;
import com.deltacodex.epadmins.model.StatusBarUtils;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class Update_movies_Fragment extends Fragment {
    private RecyclerView recyclerView;
    private MovieAdapter adapter;
    private List<MovieModel> movieList;
    private FirebaseFirestore db;
    private ProgressBar progressBar;

    public Update_movies_Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update_movies, container, false);
        if (getActivity() != null) {
            StatusBarUtils.applyGradientStatusBar(getActivity());  // Pass the Activity context
        }
        db = FirebaseFirestore.getInstance();

        recyclerView = view.findViewById(R.id.recycler_view_movie);
        progressBar = view.findViewById(R.id.progress_bar);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        movieList = new ArrayList<>();
        adapter = new MovieAdapter(getContext(), movieList, this);
        recyclerView.setAdapter(adapter);
        adapter.attachSwipeToUpdate(recyclerView);
        showSwipeInstruction();
        fetchMovies();
        return view;
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


    private void fetchMovies() {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("movies") // Make sure "Movies" is your collection name
                .addSnapshotListener((value, error) -> {
                    progressBar.setVisibility(View.GONE);

                    if (error != null) {
                        Log.e("Firebase", "Error listening for updates.", error);
                        return;
                    }

                    if (value != null) {
                        movieList.clear(); // Clear old data to avoid duplicates
                        for (DocumentSnapshot document : value.getDocuments()) {
                            MovieModel movie = document.toObject(MovieModel.class);
                            movieList.add(movie);
                        }
                        adapter.notifyDataSetChanged(); // Refresh RecyclerView
                    }
                });
    }


    public void showUpdateDialog(MovieModel movies) {
        UpdateMovieDialog dialog = UpdateMovieDialog.newInstance(movies);
        dialog.show(getParentFragmentManager(), "UpdateMovieDialog");
    }
}