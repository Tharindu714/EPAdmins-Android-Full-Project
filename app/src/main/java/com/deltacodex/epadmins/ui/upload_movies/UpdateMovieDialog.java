package com.deltacodex.epadmins.ui.upload_movies;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import com.bumptech.glide.Glide;
import com.deltacodex.epadmins.R;
import com.deltacodex.epadmins.model.MovieModel;
import com.deltacodex.epadmins.model.StatusBarUtils;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class UpdateMovieDialog extends DialogFragment {
    private EditText edtName, edtImdbRating, edtRottenTomatoes, edtGenre, downloadLinkField, trailerLinkField;
    private MovieModel movies;

    public UpdateMovieDialog() {
        // Required empty public constructor
    }
    public static UpdateMovieDialog newInstance(MovieModel movies) {
        UpdateMovieDialog dialog = new UpdateMovieDialog();
        Bundle args = new Bundle();
        args.putSerializable("movies", movies); // Ensure MovieModel implements Serializable
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setLayout(width, height);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_update_movie, container, false);

        // Retrieve MovieModel from arguments
        if (getArguments() != null) {
            movies = (MovieModel) getArguments().getSerializable("movies");
        }

        // Initialize Views
        edtName = view.findViewById(R.id.edt_name);
        edtImdbRating = view.findViewById(R.id.edt_imdb_rating);
        edtRottenTomatoes = view.findViewById(R.id.edt_rotten_tomatoes);
        edtGenre = view.findViewById(R.id.edt_genre);
        downloadLinkField = view.findViewById(R.id.edit_download_link);
        trailerLinkField = view.findViewById(R.id.edit_trailer_link);
        ImageView img_thumbnail_movie = view.findViewById(R.id.img_thumbnail_movie);
        Button btnSave = view.findViewById(R.id.btn_save);

        if (movies != null) {
            // Prefill Data
            edtName.setText(movies.getMovie_name());
            edtImdbRating.setText(movies.getMovie_imdb());
            edtRottenTomatoes.setText(movies.getMovie_rottenTomatoes());
            edtGenre.setText(movies.getMovie_genre());
            downloadLinkField.setText(movies.getMovie_downloadLink());
            trailerLinkField.setText(movies.getMovie_trailerLink());

            // Load Thumbnail using Glide
            Glide.with(requireContext())
                    .load(movies.getMovie_thumbnailUrl())
                    .into(img_thumbnail_movie);
        }

        // Handle Save Button Click
        btnSave.setOnClickListener(v -> saveUpdatedMovie());

        if (getActivity() != null) {
            StatusBarUtils.applyGradientStatusBar(getActivity());  // Pass the Activity context
        }
        return view;
    }

    private void saveUpdatedMovie() {
        if (movies == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Check if Movie ID is null before updating
        if (movies.getM_id() == null || movies.getM_id().isEmpty()) {
            Toast.makeText(getContext(), "Error: Movie ID is missing!", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("Movie_name", edtName.getText().toString().trim());
        updatedData.put("Movie_imdb", edtImdbRating.getText().toString().trim());
        updatedData.put("Movie_rottenTomatoes", edtRottenTomatoes.getText().toString().trim());
        updatedData.put("Movie_genre", edtGenre.getText().toString().trim());
        updatedData.put("Movie_downloadLink", downloadLinkField.getText().toString().trim());
        updatedData.put("Movie_trailerLink", trailerLinkField.getText().toString().trim());

        db.collection("movies").document(movies.getM_id())
                .update(updatedData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Updated Successfully!", Toast.LENGTH_SHORT).show();
                    dismiss();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Update Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        return dialog;
    }

}

