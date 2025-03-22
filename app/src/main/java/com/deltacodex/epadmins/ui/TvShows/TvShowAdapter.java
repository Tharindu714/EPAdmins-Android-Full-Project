package com.deltacodex.epadmins.ui.TvShows;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.deltacodex.epadmins.R;
import com.deltacodex.epadmins.model.TvShowModel;

import java.util.List;

public class TvShowAdapter extends RecyclerView.Adapter<TvShowAdapter.ViewHolder> {

    private final Context context;
    private final List<TvShowModel> tvShowList;
    private final Update_tv_shows_Fragment fragment;

    // Constructor for the Adapter
    public TvShowAdapter(Context context, List<TvShowModel> tvShowList, Update_tv_shows_Fragment fragment) {
        this.context = context;
        this.tvShowList = tvShowList;
        this.fragment = fragment;
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView tvShowName, imdbRating, rottenTomatoes, genre;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.image_tv_show);
            tvShowName = itemView.findViewById(R.id.text_tv_shows);
            imdbRating = itemView.findViewById(R.id.textm_imdb_rating);
            rottenTomatoes = itemView.findViewById(R.id.textm_rotten_tomatoes);
            genre = itemView.findViewById(R.id.textm_genre);
        }
    }

    // onCreateViewHolder() creates a ViewHolder for each item
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout for each row
        View view = LayoutInflater.from(context).inflate(R.layout.tv_show_list_item, parent, false);
        return new ViewHolder(view);  // Return the new ViewHolder
    }

    // onBindViewHolder() binds the data to the views for each row
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TvShowModel tvShow = tvShowList.get(position);

        // Set data for each item view
        holder.tvShowName.setText(tvShow.getName());
        holder.imdbRating.setText("IMDB: " + tvShow.getImdb());
        holder.rottenTomatoes.setText("Rotten Tomatoes: " + tvShow.getRottenTomatoes() + "%");
        holder.genre.setText("Genre: " + tvShow.getGenre());

        // Load image using Glide
        Glide.with(context)
                .load(tvShow.getThumbnailUrl())
                .placeholder(R.drawable.premiere_wave_studio_inc)  // Placeholder while loading
                .into(holder.thumbnail);
    }

    // getItemCount() returns the total number of items
    @Override
    public int getItemCount() {
        return tvShowList.size();
    }

    // Attach swipe-to-update functionality
    public void attachSwipeToUpdate(RecyclerView recyclerView) {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false; // No need for drag & drop
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                TvShowModel tvShow = tvShowList.get(position);

                // Open Update Dialog
                fragment.showUpdateDialog(tvShow);

                // Refresh item to prevent it from disappearing
                new Handler().postDelayed(() -> notifyItemChanged(position), 300);
            }
        }).attachToRecyclerView(recyclerView);
    }
}



