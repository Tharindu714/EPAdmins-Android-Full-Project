package com.deltacodex.epadmins.ui.upload_movies;

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
import com.deltacodex.epadmins.model.MovieModel;
import java.util.List;


public class MovieAdapter  extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {
    private final Context context;
    private final List<MovieModel> MovieList;
    private final Update_movies_Fragment fragment;

    public MovieAdapter(Context context, List<MovieModel> MovieList, Update_movies_Fragment fragment) {
        this.context = context;
        this.MovieList = MovieList;
        this.fragment = fragment;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView MovieName, imdbRating, rottenTomatoes, genre;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.image_movie);
            MovieName = itemView.findViewById(R.id.text_movie_name);
            imdbRating = itemView.findViewById(R.id.textm_imdb_rating);
            rottenTomatoes = itemView.findViewById(R.id.textm_rotten_tomatoes);
            genre = itemView.findViewById(R.id.textm_genre);
        }
    }
    // onCreateViewHolder() creates a ViewHolder for each item
    @NonNull
    @Override
    public MovieAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout for each row
        View view = LayoutInflater.from(context).inflate(R.layout.movie_list_item, parent, false);
        return new MovieAdapter.ViewHolder(view);  // Return the new ViewHolder
    }

    // onBindViewHolder() binds the data to the views for each row
    @Override
    public void onBindViewHolder(@NonNull MovieAdapter.ViewHolder holder, int position) {
        MovieModel movies = MovieList.get(position);

        // Set data for each item view
        holder.MovieName.setText(movies.getMovie_name());
        holder.imdbRating.setText("IMDB: " + movies.getMovie_imdb());
        holder.rottenTomatoes.setText("Rotten Tomatoes: " + movies.getMovie_rottenTomatoes() + "%");
        holder.genre.setText("Genre: " + movies.getMovie_genre());

        // Load image using Glide
        Glide.with(context)
                .load(movies.getMovie_thumbnailUrl())
                .placeholder(R.drawable.premiere_wave_studio_inc)  // Placeholder while loading
                .into(holder.thumbnail);
    }

    // getItemCount() returns the total number of items
    @Override
    public int getItemCount() {
        return MovieList.size();
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
                MovieModel movies = MovieList.get(position);

                // Open Update Dialog
                fragment.showUpdateDialog(movies);

                // Refresh item to prevent it from disappearing
                new Handler().postDelayed(() -> notifyItemChanged(position), 300);
            }
        }).attachToRecyclerView(recyclerView);
    }
}
