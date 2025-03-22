package com.deltacodex.epadmins.ui.TvShows;

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
import com.deltacodex.epadmins.model.StatusBarUtils;
import com.deltacodex.epadmins.model.TvShowModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UpdateTvShowDialog extends DialogFragment {

    private EditText edtName, edtImdbRating, edtRottenTomatoes, edtGenre, downloadLinkField, trailerLinkField;
    private ImageView imgThumbnail;
    private Button btnSave;
    private TvShowModel tvShow;

    public UpdateTvShowDialog() {
        // Required empty public constructor
    }

    public static UpdateTvShowDialog newInstance(TvShowModel tvShow) {
        UpdateTvShowDialog dialog = new UpdateTvShowDialog();
        Bundle args = new Bundle();
        args.putSerializable("tv_shows", tvShow); // Ensure TvShowModel implements Serializable
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
        View view = inflater.inflate(R.layout.dialog_update_tv_show, container, false);

        // Retrieve TvShowModel from arguments
        if (getArguments() != null) {
            tvShow = (TvShowModel) getArguments().getSerializable("tv_shows");
        }

        // Initialize Views
        edtName = view.findViewById(R.id.edt_name);
        edtImdbRating = view.findViewById(R.id.edt_imdb_rating);
        edtRottenTomatoes = view.findViewById(R.id.edt_rotten_tomatoes);
        edtGenre = view.findViewById(R.id.edt_genre);
        downloadLinkField = view.findViewById(R.id.edit_download_link);
        trailerLinkField = view.findViewById(R.id.edit_trailer_link);
        imgThumbnail = view.findViewById(R.id.img_thumbnail);
        btnSave = view.findViewById(R.id.btn_save);

        if (tvShow != null) {
            // Prefill Data
            edtName.setText(tvShow.getName());
            edtImdbRating.setText(tvShow.getImdb());
            edtRottenTomatoes.setText(tvShow.getRottenTomatoes());
            edtGenre.setText(tvShow.getGenre());
            downloadLinkField.setText(tvShow.getDownloadLink());
            trailerLinkField.setText(tvShow.getTrailerLink());

            // Load Thumbnail using Glide
            Glide.with(requireContext())
                    .load(tvShow.getThumbnailUrl())
                    .into(imgThumbnail);
        }

        // Handle Save Button Click
        btnSave.setOnClickListener(v -> saveUpdatedTvShow());

        if (getActivity() != null) {
            StatusBarUtils.applyGradientStatusBar(getActivity());  // Pass the Activity context
        }
        return view;
    }

    private void saveUpdatedTvShow() {
        if (tvShow == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Check if TV Show ID is null before updating
        if (tvShow.getId() == null || tvShow.getId().isEmpty()) {
            Toast.makeText(getContext(), "Error: TV Show ID is missing!", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("name", edtName.getText().toString().trim());
        updatedData.put("imdb", edtImdbRating.getText().toString().trim());
        updatedData.put("rottenTomatoes", edtRottenTomatoes.getText().toString().trim());
        updatedData.put("genre", edtGenre.getText().toString().trim());
        updatedData.put("downloadLink", downloadLinkField.getText().toString().trim());
        updatedData.put("trailerLink", trailerLinkField.getText().toString().trim());

        db.collection("tv_shows").document(tvShow.getId())
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
