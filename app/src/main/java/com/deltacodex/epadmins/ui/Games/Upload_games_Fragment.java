package com.deltacodex.epadmins.ui.Games;

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

public class Upload_games_Fragment extends Fragment {
    private FirebaseFirestore mFirestore;

    private EditText GameName, GameDescription, initial_date, genre, developer, platform, downloadLink, trailerLink;
    private EditText thumbnailUrlInput, largeImageUrlInput;  // New fields for manual image URL input
    private boolean isThumbnailSelected;
    public Upload_games_Fragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upload_games, container, false);
        if (getActivity() != null) {
            StatusBarUtils.applyGradientStatusBar(getActivity());  // Pass the Activity context
        }
        mFirestore = FirebaseFirestore.getInstance();

        GameName = view.findViewById(R.id.Games_name1);
        GameDescription = view.findViewById(R.id.Games_description1);
        initial_date = view.findViewById(R.id.release_date);
        genre = view.findViewById(R.id.Genere2);
        developer = view.findViewById(R.id.developer);
        platform = view.findViewById(R.id.Platform);
        downloadLink = view.findViewById(R.id.steam_link);
        trailerLink = view.findViewById(R.id.yt_link2);

        thumbnailUrlInput = view.findViewById(R.id.thumbnail_img_url2); // New input field
        largeImageUrlInput = view.findViewById(R.id.large_img_url2); // New input field

        // Click listener for Thumbnail URL field
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
        uploadButton.setOnClickListener(v -> uploadGame());

        StatusBarUtils.applyGradientStatusBar(getActivity());
        return view;
    }

    private void openGoogleImageSearch() {
        String GameTitle = GameName.getText().toString().trim();
        if (GameTitle.isEmpty()) {
            Toast.makeText(getActivity(), "Enter a movie name first!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Google Image search URL
        String googleSearchUrl = "https://www.google.com/search?tbm=isch&q=" + Uri.encode(GameTitle);

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

    private void uploadGame() {
        String thumbnailUrl = thumbnailUrlInput.getText().toString().trim();
        String largeImageUrl = largeImageUrlInput.getText().toString().trim();
        String GameTitle = GameName.getText().toString();
        String released = initial_date.getText().toString();
        String Genre = genre.getText().toString();
        String Platform = platform.getText().toString();
        String download = downloadLink.getText().toString();
        String trailer = trailerLink.getText().toString();

        if (GameTitle.isEmpty() ||
                released.isEmpty() ||
                Platform.isEmpty() ||
                download.isEmpty() ||
                trailer.isEmpty() ||
                thumbnailUrl.isEmpty() || largeImageUrl.isEmpty()) {
            Toast.makeText(getActivity(), "Please Fill all Required Fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String uniqueId = UUID.randomUUID().toString(); // Unique ID for each TV show

        Map<String, Object> Games = new HashMap<>();
        Games.put("g_id", uniqueId);
        Games.put("name", GameTitle);
        Games.put("description", GameDescription.getText().toString());
        Games.put("Released_Date", released);
        Games.put("genre", Genre);
        Games.put("Developer", developer.getText().toString());
        Games.put("Platforms", Platform);
        Games.put("downloadLink", download);
        Games.put("trailerLink", trailer);
        Games.put("thumbnailUrl", thumbnailUrl);
        Games.put("largeImageUrl", largeImageUrl);
        Games.put("status", "approved");;

        mFirestore.collection("games").document(uniqueId)
                .set(Games)
                .addOnSuccessListener(aVoid -> {
                    GameName.setText("");
                    GameDescription.setText("");
                    initial_date.setText("");
                    genre.setText("");
                    developer.setText("");
                    platform.setText("");
                    downloadLink.setText("");
                    trailerLink.setText("");
                    thumbnailUrlInput.setText("");
                    largeImageUrlInput.setText("");
//                    Toast.makeText(getActivity(), "TV Show uploaded successfully!", Toast.LENGTH_SHORT).show();
                    showNotification(GameTitle, largeImageUrl);
                })
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Firestore Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());

    }

    private void showNotification(String gameName, String imageUrl) {
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
                        .setContentTitle("New Game Added")
                        .setContentText(gameName + "is added to Game Collection Just Now")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)  // Ensure visibility
                        .setDefaults(Notification.DEFAULT_ALL)  // Enable sound, vibration, lights
                        .setLargeIcon(largeIcon)
                        .setStyle(
                                new NotificationCompat.BigPictureStyle()
                                        .bigPicture(largeIcon)
                                        .setSummaryText(gameName + " is added to Game Collection Just Now")
                        )
                        .build();


                notificationManager.notify(1, notification);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}