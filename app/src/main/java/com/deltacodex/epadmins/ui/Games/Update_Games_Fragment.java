package com.deltacodex.epadmins.ui.Games;

import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;
import com.deltacodex.epadmins.R;
import com.deltacodex.epadmins.model.GameModel;
import com.deltacodex.epadmins.model.MovieModel;
import com.deltacodex.epadmins.model.StatusBarUtils;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Update_Games_Fragment extends Fragment {
    private RecyclerView recyclerView;
    private GameAdapter gameAdapter;
    private List<GameModel> gameList;
    private FirebaseFirestore firestore;

    public Update_Games_Fragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_update_games, container, false);
        if (getActivity() != null) {
            StatusBarUtils.applyGradientStatusBar(getActivity());  // Pass the Activity context
        }
        firestore = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.game_list_recycler);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        gameList = new ArrayList<>();
        gameAdapter = new GameAdapter(getContext(), gameList, this);
        recyclerView.setAdapter(gameAdapter);
        gameAdapter.attachSwipeToUpdate(recyclerView);
        loadGames();
        attachSwipeHelper(recyclerView);
        showSwipeInstruction();
        return view;
    }


    private void loadGames() {
        firestore.collection("games") // Make sure "Movies" is your collection name
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("Firebase", "Error listening for updates.", error);
                        return;
                    }

                    if (value != null) {
                        gameList.clear(); // Clear old data to avoid duplicates
                        for (DocumentSnapshot document : value.getDocuments()) {
                            GameModel gamess = document.toObject(GameModel.class);
                            gameList.add(gamess);
                        }
                        gameAdapter.notifyDataSetChanged(); // Refresh RecyclerView
                    }
                });
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

    // Attach the swipe helper to handle right swipe animation
    private void attachSwipeHelper(RecyclerView recyclerView) {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Handle the swipe action
                int position = viewHolder.getAdapterPosition();
                GameModel swipedGame = gameList.get(position);
                onGameSwiped(swipedGame, position);  // Call method to handle action
            }

            @Override
            public void onChildDraw(@NonNull Canvas c,
                                    @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY,
                                    int actionState,
                                    boolean isCurrentlyActive) {
                // Create fade-out effect and move right while swiping
                View itemView = viewHolder.itemView;
                float alpha = 1.0f - Math.abs(dX) / (float) itemView.getWidth();
                itemView.setAlpha(alpha);
                itemView.setTranslationX(dX);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        });

        // Attach ItemTouchHelper to RecyclerView
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    // Handle what happens when a game is swiped
    public void onGameSwiped(GameModel game, int position) {
        // Example: You can remove the game from the list or show an update dialog
        UpdateGameDialog dialog = new UpdateGameDialog(game);
        dialog.show(getChildFragmentManager(), "UpdateGameDialog");
    }
}