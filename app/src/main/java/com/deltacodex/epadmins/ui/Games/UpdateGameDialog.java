package com.deltacodex.epadmins.ui.Games;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.deltacodex.epadmins.R;
import com.deltacodex.epadmins.model.GameModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UpdateGameDialog extends DialogFragment {
    private final GameModel game;
    private FirebaseFirestore firestore;

    private EditText platformInput, downloadInput, trailerInput;
    TextView gameName;

    public UpdateGameDialog(GameModel game) {
        this.game = game;
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

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        return dialog;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_update_games, container, false);

        platformInput = view.findViewById(R.id.edit_platform);
        downloadInput = view.findViewById(R.id.edit_download_link);
        trailerInput = view.findViewById(R.id.edit_trailer_link);
        ImageView gameCover = view.findViewById(R.id.game_large_cover);
        gameName = view.findViewById(R.id.game_title);
        firestore = FirebaseFirestore.getInstance();

        if (game != null) {
            // Prefill Data
            platformInput.setText(game.getPlatforms());
            downloadInput.setText(game.getDownloadLink());
            trailerInput.setText(game.getTrailerLink());
            gameName.setText(game.getName());
            // Load Thumbnail using Glide
            Glide.with(requireContext())
                    .load(game.getLargeImageUrl())
                    .into(gameCover);
        }
        view.findViewById(R.id.btn_update_game).setOnClickListener(v -> updateGameInfo());
        return view;
    }

    private void updateGameInfo() {
        if (game == null) return;

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Check if TV Show ID is null before updating
        if (game.getG_id() == null || game.getG_id().isEmpty()) {
            Toast.makeText(getContext(), "Error: Game ID is missing!", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, Object> updates = new HashMap<>();
        updates.put("Platforms", platformInput.getText().toString().trim());
        updates.put("downloadLink", downloadInput.getText().toString().trim());
        updates.put("trailerLink", trailerInput.getText().toString().trim());

        firestore.collection("games").document(game.getG_id())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Game updated!", Toast.LENGTH_SHORT).show();
                    dismiss();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Update failed!", Toast.LENGTH_SHORT).show());
    }
}

