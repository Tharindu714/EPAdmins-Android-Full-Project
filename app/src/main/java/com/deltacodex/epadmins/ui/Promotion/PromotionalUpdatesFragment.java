package com.deltacodex.epadmins.ui.Promotion;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.deltacodex.epadmins.R;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PromotionalUpdatesFragment extends Fragment {

    private EditText videoUrlInput, trailerHeadline, releaseDate, trailerDescription, posterHeadline ;
    private WebView videoPreview, blooperPreview, TeasersPreview;

    private FirebaseFirestore db;
    private boolean isThumbnailSelected;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_promotional_updates, container, false);

        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        videoUrlInput = view.findViewById(R.id.videoUrlInput);
        trailerHeadline = view.findViewById(R.id.trailerHeadline);
        releaseDate = view.findViewById(R.id.releaseDate);
        trailerDescription = view.findViewById(R.id.trailerDescription);
        Button uploadTrailerBtn = view.findViewById(R.id.uploadTrailerBtn);
        videoPreview = view.findViewById(R.id.videoPreview); // WebView for YouTube preview
        videoUrlInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                loadYouTubePreview(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // Initialize Poster Upload Components
        EditText imageUrlInput = view.findViewById(R.id.imageUrlInput);
        posterHeadline = view.findViewById(R.id.posterHeadline);
        EditText posterDescription = view.findViewById(R.id.posterDescription);
        ImageView imagePreview = view.findViewById(R.id.imagePreview);
        Button uploadPosterBtn = view.findViewById(R.id.uploadPosterBtn);
        imageUrlInput.setOnClickListener(v -> {
            isThumbnailSelected = true;
            openGoogleImageSearch();
        });
        imageUrlInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                loadImagePreview(imagePreview, charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        EditText blooperUrlInput = view.findViewById(R.id.blooper_videoUrlInput);
        EditText blooperHeadline = view.findViewById(R.id.blooper_Headline);
        blooperPreview = view.findViewById(R.id.blooper_videoPreview);
        Button uploadBlooperBtn = view.findViewById(R.id.uploadblooper_Btn);

        blooperUrlInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                loadYouTube_blooperPreview(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        EditText TeaserUrlInput = view.findViewById(R.id.teasers_videoUrlInput);
        EditText TeaserHeadline = view.findViewById(R.id.teasers_Headline);
        TeasersPreview = view.findViewById(R.id.teasers_videoPreview);
        Button uploadTeaserBtn = view.findViewById(R.id.uploadteasers_Btn);

        TeaserUrlInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                loadYouTube_TeaserPreview(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // Handle upload button click
        uploadTrailerBtn.setOnClickListener(v -> uploadTrailerToFirebase());
        uploadPosterBtn.setOnClickListener(v -> uploadPosterToFirebase(imageUrlInput, posterHeadline, posterDescription, imagePreview));
        uploadBlooperBtn.setOnClickListener(v -> uploadBlooperToFirebase(blooperUrlInput, blooperHeadline));
        uploadTeaserBtn.setOnClickListener(v -> uploadTeasersToFirebase(TeaserUrlInput, TeaserHeadline));

        return view;
    }

    private void loadYouTubePreview(String url) {
        String videoId = extractYouTubeVideoId(url);

        if (!videoId.isEmpty()) {
            String embedUrl = "https://www.youtube.com/embed/" + videoId;
            String iframeHtml = "<html><body><iframe width=\"100%\" height=\"100%\" src=\"" + embedUrl + "\" frameborder=\"0\" allowfullscreen></iframe></body></html>";

            videoPreview.getSettings().setJavaScriptEnabled(true);
            videoPreview.loadData(iframeHtml, "text/html", "utf-8");
        }
    }

    private void openGoogleImageSearch() {
        String posterTitle = posterHeadline.getText().toString().trim();
        if (posterTitle.isEmpty()) {
            Toast.makeText(getActivity(), "Enter a Poster Title First!", Toast.LENGTH_LONG).show();
            return;
        }

        // Google Image search URL
        String googleSearchUrl = "https://www.google.com/search?tbm=isch&q=" + Uri.encode(posterTitle);

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

    private void loadYouTube_blooperPreview(String url) {
        String videoId = extractYouTubeVideoId(url);

        if (!videoId.isEmpty()) {
            String embedUrl = "https://www.youtube.com/embed/" + videoId;
            String iframeHtml = "<html><body><iframe width=\"100%\" height=\"100%\" src=\"" + embedUrl + "\" frameborder=\"0\" allowfullscreen></iframe></body></html>";

            blooperPreview.getSettings().setJavaScriptEnabled(true);
            blooperPreview.loadData(iframeHtml, "text/html", "utf-8");
        }
    }

    private void loadYouTube_TeaserPreview(String url) {
        String videoId = extractYouTubeVideoId(url);

        if (!videoId.isEmpty()) {
            String embedUrl = "https://www.youtube.com/embed/" + videoId;
            String iframeHtml = "<html><body><iframe width=\"100%\" height=\"100%\" src=\"" + embedUrl + "\" frameborder=\"0\" allowfullscreen></iframe></body></html>";

            TeasersPreview.getSettings().setJavaScriptEnabled(true);
            TeasersPreview.loadData(iframeHtml, "text/html", "utf-8");
        }
    }

    private void loadImagePreview(ImageView imageView, String url) {
        if (!url.isEmpty()) {
            Glide.with(getContext()).load(url).into(imageView);
        } else {
            imageView.setImageResource(0); // Clear preview if URL is empty
        }
    }


    private String extractYouTubeVideoId(String url) {
        String pattern = "(?:(?:https?:\\/\\/)?(?:www\\.)?(?:youtube\\.com\\/.*(?:\\?|&)v=|youtu\\.be\\/|youtube\\.com\\/embed\\/|youtube\\.com\\/v\\/))([a-zA-Z0-9_-]{11})";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(url);
        return matcher.find() ? matcher.group(1) : "";
    }

    private void uploadTrailerToFirebase() {
        String videoUrl = videoUrlInput.getText().toString().trim();
        String headline = trailerHeadline.getText().toString().trim();
        String date = releaseDate.getText().toString().trim();
        String description = trailerDescription.getText().toString().trim();

        if (videoUrl.isEmpty() || headline.isEmpty() || date.isEmpty() || description.isEmpty()) {
            Toast.makeText(getContext(), "All fields are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        String videoId = extractYouTubeVideoId(videoUrl);
        if (videoId.isEmpty()) {
            Toast.makeText(getContext(), "Invalid YouTube URL!", Toast.LENGTH_SHORT).show();
            return;
        }

        String thumbnailUrl = "https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg"; // Get YouTube thumbnail

        // Create a trailer object
        Map<String, Object> trailerData = new HashMap<>();
        trailerData.put("videoUrl_trailer", videoUrl);
        trailerData.put("videoId_trailer", videoId);
        trailerData.put("thumbnailUrl_trailer", thumbnailUrl);
        trailerData.put("headline_trailer", headline);
        trailerData.put("releaseDate_trailer", date);
        trailerData.put("description_trailer", description);
        trailerData.put("timestamp_trailer", FieldValue.serverTimestamp());

        db.collection("New_Trailers")
                .add(trailerData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Trailer uploaded successfully!", Toast.LENGTH_SHORT).show();

                    // Clear input fields
                    videoUrlInput.setText("");
                    trailerHeadline.setText("");
                    releaseDate.setText("");
                    trailerDescription.setText("");
                    resetWebView(videoPreview);
                }).addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void uploadPosterToFirebase(EditText imageUrlInput, EditText posterHeadline, EditText posterDescription, ImageView imagePreview) {
        String imageUrl = imageUrlInput.getText().toString().trim();
        String headline = posterHeadline.getText().toString().trim();
        String description = posterDescription.getText().toString().trim();

        if (imageUrl.isEmpty() || headline.isEmpty()) {
            Toast.makeText(getContext(), "Image URL and Headline are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a poster object
        Map<String, Object> posterData = new HashMap<>();
        posterData.put("imageUrl_poster", imageUrl);
        posterData.put("headline_poster", headline);
        posterData.put("description_poster", description);
        posterData.put("timestamp_poster", FieldValue.serverTimestamp());

        db.collection("New_Poster")
                .add(posterData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Poster uploaded successfully!", Toast.LENGTH_SHORT).show();

                    // Clear input fields
                    imageUrlInput.setText("");
                    posterHeadline.setText("");
                    posterDescription.setText("");
                    imagePreview.setImageResource(0); // Clear ImageView preview
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void uploadBlooperToFirebase(EditText blooperUrlInput, EditText blooperHeadline) {
        String videoUrl = blooperUrlInput.getText().toString().trim();
        String headline = blooperHeadline.getText().toString().trim();

        if (videoUrl.isEmpty() || headline.isEmpty()) {
            Toast.makeText(getContext(), "Video URL and Headline are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        String videoId = extractYouTubeVideoId(videoUrl);
        if (videoId.isEmpty()) {
            Toast.makeText(getContext(), "Invalid YouTube URL!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate YouTube thumbnail URL
        String thumbnailUrl = "https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg";

        // Create a blooper object
        Map<String, Object> blooperData = new HashMap<>();
        blooperData.put("videoUrl_blooper", videoUrl);
        blooperData.put("videoId_blooper", videoId);
        blooperData.put("thumbnailUrl_blooper", thumbnailUrl);
        blooperData.put("headline_blooper", headline);
        blooperData.put("timestamp_blooper", FieldValue.serverTimestamp());

        db.collection("New_Bloopers")
                .add(blooperData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Blooper uploaded successfully!", Toast.LENGTH_SHORT).show();

                    // Clear input fields
                    blooperUrlInput.setText("");
                    blooperHeadline.setText("");
                    resetWebView(blooperPreview);// Clear the WebView preview
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void uploadTeasersToFirebase(EditText TeaserUrlInput, EditText TeaserHeadline) {
        String videoUrl = TeaserUrlInput.getText().toString().trim();
        String headline = TeaserHeadline.getText().toString().trim();

        if (videoUrl.isEmpty() || headline.isEmpty()) {
            Toast.makeText(getContext(), "Video URL and Headline are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        String videoId = extractYouTubeVideoId(videoUrl);
        if (videoId.isEmpty()) {
            Toast.makeText(getContext(), "Invalid YouTube URL!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Generate YouTube thumbnail URL
        String thumbnailUrl = "https://img.youtube.com/vi/" + videoId + "/hqdefault.jpg";

        // Create a blooper object
        Map<String, Object> TeasersData = new HashMap<>();
        TeasersData.put("videoUrl_Teasers", videoUrl);
        TeasersData.put("videoId_Teasers", videoId);
        TeasersData.put("thumbnailUrl_Teasers", thumbnailUrl);
        TeasersData.put("headline_Teasers", headline);
        TeasersData.put("timestamp_Teasers", FieldValue.serverTimestamp());

        db.collection("New_Teasers")
                .add(TeasersData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Teaser uploaded successfully!", Toast.LENGTH_SHORT).show();

                    // Clear input fields
                    TeaserUrlInput.setText("");
                    TeaserHeadline.setText("");
                    resetWebView(TeasersPreview);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void resetWebView(WebView webView) {
        String blackScreenHtml = "<html><body style='background-color:black;'></body></html>";
        webView.loadData(blackScreenHtml, "text/html", "utf-8");
    }
}

