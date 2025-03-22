package com.deltacodex.epadmins.ui.Games;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.deltacodex.epadmins.R;
import com.deltacodex.epadmins.model.GameModel;
import com.deltacodex.epadmins.model.MovieModel;
import com.deltacodex.epadmins.ui.upload_movies.Update_movies_Fragment;

import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {
    private final Context context;
    private final List<GameModel> gameList;
    private final Update_Games_Fragment fragment;

    public GameAdapter(Context context, List<GameModel> gameList, Update_Games_Fragment fragment) {
        this.context = context;
        this.gameList = gameList;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.game_list_item, parent, false);
        return new GameViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        GameModel game = gameList.get(position);

        holder.gameTitle.setText(game.getName());
        holder.developer.setText(game.getDeveloper());
        holder.releaseDate.setText(game.getReleased_Date());

        Glide.with(context)
                .load(game.getLargeImageUrl())
                .placeholder(R.drawable.premiere_wave_studio_inc)  // Placeholder while loading
                .into(holder.largeCover);

        Glide.with(context)
                .load(game.getThumbnailUrl())
                .placeholder(R.drawable.event_pulse_admin)  // Placeholder while loading
                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return gameList.size();
    }

    public void attachSwipeToUpdate(RecyclerView recyclerView) {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false; // No need for drag & drop
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                GameModel games = gameList.get(position);

                // Open Update Dialog
                fragment.onGameSwiped(games,position);

                // Refresh item to prevent it from disappearing
                new Handler().postDelayed(() -> notifyItemChanged(position), 300);
            }
        }).attachToRecyclerView(recyclerView);
    }

    public static class GameViewHolder extends RecyclerView.ViewHolder {
        TextView gameTitle, developer, releaseDate;
        ImageView largeCover, thumbnail;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);
            gameTitle = itemView.findViewById(R.id.gameTitle);
            developer = itemView.findViewById(R.id.developer);
            releaseDate = itemView.findViewById(R.id.releaseDate);
            largeCover = itemView.findViewById(R.id.largeCover);
            thumbnail = itemView.findViewById(R.id.thumbnail);
        }
    }
}

