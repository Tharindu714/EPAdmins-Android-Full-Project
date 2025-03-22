package com.deltacodex.epadmins.ui.upload_movies;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.deltacodex.epadmins.R;
import com.deltacodex.epadmins.model.StatusBarUtils;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Upload_movies_Fragment extends Fragment {

    private FirebaseFirestore mFirestore;

    private EditText movieName, movieDescription, imdbRatings, rottenTomatoes, userLove, creator, genre, Duration, distributed, downloadLink, trailerLink;
    private EditText thumbnailUrlInput, largeImageUrlInput;  // New fields for manual image URL input
    private boolean isThumbnailSelected;
    public Upload_movies_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_movies, container, false);

        mFirestore = FirebaseFirestore.getInstance();

        movieName = view.findViewById(R.id.Games_name1);
        movieDescription = view.findViewById(R.id.Games_description1);
        imdbRatings = view.findViewById(R.id.imdb_ratings1);
        rottenTomatoes = view.findViewById(R.id.rt1);
        userLove = view.findViewById(R.id.user_love1);
        creator = view.findViewById(R.id.release_date);
        genre = view.findViewById(R.id.Genere2);
        Duration = view.findViewById(R.id.developer);
        distributed = view.findViewById(R.id.Platform);
        downloadLink = view.findViewById(R.id.steam_link);
        trailerLink = view.findViewById(R.id.yt_link2);

        thumbnailUrlInput = view.findViewById(R.id.thumbnail_img_url2); // New input field
        largeImageUrlInput = view.findViewById(R.id.large_img_url2); // New input field

        thumbnailUrlInput.setOnClickListener(v -> {
            isThumbnailSelected = true;
            openGoogleImageSearch();
        });

        // Click listener for Large Image URL field
        largeImageUrlInput.setOnClickListener(v -> {
            isThumbnailSelected = false;
            openGoogleImageSearch();
        });

        Button uploadButton = view.findViewById(R.id.submit_changes3);
        uploadButton.setOnClickListener(v -> uploadMovies());
        if (getActivity() != null) {
            StatusBarUtils.applyGradientStatusBar(getActivity());  // Pass the Activity context
        }
        return view;
    }

    private void openGoogleImageSearch() {
        String movieTitle = movieName.getText().toString().trim();
        if (movieTitle.isEmpty()) {
            Toast.makeText(getActivity(), "Enter a movie name first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Google Image search URL
        String googleSearchUrl = "https://www.google.com/search?tbm=isch&q=" + Uri.encode(movieTitle);

        // Create an Intent to open Google Chrome with the image search URL
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(googleSearchUrl));
        intent.putExtra("searchUrl", googleSearchUrl);

        // Try to explicitly open Google Chrome by targeting its package name
        intent.setPackage("com.android.chrome");

        // Check if Google Chrome is available
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            // If Chrome is not installed, fall back to the default browser
            Toast.makeText(getActivity(), "Google Chrome is not installed. Falling back to the default browser.", Toast.LENGTH_SHORT).show();
            // If Chrome is not installed, use the default browser
            intent.setPackage(null); // This ensures it will be handled by any available browser
            startActivity(intent);
        }
    }
    private void uploadMovies() {
        String thumbnailUrl = thumbnailUrlInput.getText().toString().trim();
        String largeImageUrl = largeImageUrlInput.getText().toString().trim();
        String movieTitle = movieName.getText().toString();
        String movieDesc = movieDescription.getText().toString();
        String imdb = imdbRatings.getText().toString();
        String rt_rating = rottenTomatoes.getText().toString();
        String loved_users = userLove.getText().toString();
        String created_by = creator.getText().toString();
        String Genre = genre.getText().toString();
        String duration = Duration.getText().toString();
        String distributedBy = distributed.getText().toString();
        String download = downloadLink.getText().toString();
        String trailer = trailerLink.getText().toString();

        if (movieTitle.isEmpty() ||
                imdb.isEmpty() ||
                rt_rating.isEmpty() ||
                loved_users.isEmpty() ||
                download.isEmpty() ||
                trailer.isEmpty() ||
                thumbnailUrl.isEmpty() || largeImageUrl.isEmpty()) {
            Toast.makeText(getActivity(), "Please Fill all Required Fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String uniqueId = UUID.randomUUID().toString(); // Unique ID for each TV show

        Map<String, Object> moviesSet = new HashMap<>();
        moviesSet.put("m_id", uniqueId);
        moviesSet.put("Movie_name", movieTitle);
        moviesSet.put("Movie_description", movieDesc);
        moviesSet.put("Movie_imdb", imdb);
        moviesSet.put("Movie_rottenTomatoes", rt_rating);
        moviesSet.put("Movie_userLove", loved_users);
        moviesSet.put("Movie_creator", created_by);
        moviesSet.put("Movie_genre", Genre);
        moviesSet.put("Movie_duration", duration);
        moviesSet.put("Movie_distributed", distributedBy);
        moviesSet.put("Movie_downloadLink", download);
        moviesSet.put("Movie_trailerLink", trailer);
        moviesSet.put("Movie_thumbnailUrl", thumbnailUrl);
        moviesSet.put("Movie_largeImageUrl", largeImageUrl);
        moviesSet.put("status", "approved");;

        mFirestore.collection("movies").document(uniqueId)
                .set(moviesSet)
                .addOnSuccessListener(aVoid -> {
                    movieName.setText("");
                    movieDescription.setText("");
                    imdbRatings.setText("");
                    rottenTomatoes.setText("");
                    userLove.setText("");
                    creator.setText("");
                    genre.setText("");
                    Duration.setText("");
                    distributed.setText("");
                    downloadLink.setText("");
                    trailerLink.setText("");
                    thumbnailUrlInput.setText("");
                    largeImageUrlInput.setText("");
                    showNotification(movieTitle, largeImageUrl);
                })
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Firestore Error: " + e.getMessage(), Toast.LENGTH_LONG).show());

    }

    private void showNotification(String movieName, String imageUrl) {
        Context context = getActivity();
        if (context == null) return; // Prevent null errors

        String CHANNEL_ID = "upload_success_channel";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Create Notification Channel (for Android 8.0+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Upload Notifications", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        // Load Image with Glide
        new Thread(() -> {
            try {
                Bitmap largeIcon = Glide.with(context)
                        .asBitmap()
                        .load(imageUrl)
                        .submit()
                        .get(); // Synchronously fetch image

                Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.splash)
                        .setContentTitle("New Movie Added")
                        .setContentText(movieName + "is added to Movie Collection Just Now")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)  // Ensure visibility
                        .setDefaults(Notification.DEFAULT_ALL)  // Enable sound, vibration, lights
                        .setLargeIcon(largeIcon)
                        .setStyle(
                                new NotificationCompat.BigPictureStyle()
                                        .bigPicture(largeIcon)
                                        .setSummaryText(movieName + " is added to Movie Collection Just Now")
                        )
                        .build();


                notificationManager.notify(1, notification);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}