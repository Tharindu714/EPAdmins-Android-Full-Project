package com.deltacodex.epadmins;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.deltacodex.epadmins.model.StatusBarUtils;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ChartActivity extends AppCompatActivity {
    private PieChart pieChart;
    private BarChart barChart;
    private LineChart lineChart;
    private PieChart donutChart,donutChart2,donutChart3;
    private FirebaseFirestore db;
    private final Map<String, Integer> genreCountMapTVShows = new HashMap<>();
    private final Map<String, Integer> genreCountMapMovies = new HashMap<>();
    private final Map<String, Integer> genreCountMapGames = new HashMap<>();

    private final Map<String, Integer> genreColors = new HashMap<String, Integer>() {{
        put("Horror", Color.parseColor("#8B0000"));  // Dark Red
        put("Action", Color.parseColor("#FF4500"));  // Dark Orange
        put("Drama", Color.parseColor("#800080"));   // Purple
        put("Comedy", Color.parseColor("#006400"));  // Dark Green
        put("Sci-Fi", Color.parseColor("#4682B4"));  // Steel Blue
        put("Thriller", Color.parseColor("#2F4F4F"));// Dark Slate Gray
        put("Romance", Color.parseColor("#C71585")); // Deep Pink
        put("Other", Color.parseColor("#020950"));   // Dark Gray
    }};

    private final Map<String, Integer> genreColorsMovies = new HashMap<String, Integer>() {{
        put("Horror", Color.parseColor("#B22222"));  // Firebrick Red
        put("Action", Color.parseColor("#FF6347"));  // Tomato Red
        put("Drama", Color.parseColor("#8A2BE2"));   // Blue Violet
        put("Comedy", Color.parseColor("#32CD32"));  // Lime Green
        put("Sci-Fi", Color.parseColor("#20B2AA"));  // Light Sea Green
        put("Thriller", Color.parseColor("#2E8B57")); // Sea Green
        put("Romance", Color.parseColor("#FF1493")); // Deep Pink
        put("Other", Color.parseColor("#01332B"));   // Dark Gray
    }};

    private final Map<String, Integer> genreColorsGames = new HashMap<String, Integer>() {{
        put("Horror", Color.parseColor("#FF0000"));  // Red
        put("Action", Color.parseColor("#C52F02"));  // Gold
        put("Racing", Color.parseColor("#0000FF"));   // Blue
        put("Shooter", Color.parseColor("#FF8C00")); // Dark Orange
        put("Adventure", Color.parseColor("#FF69B4")); // Hot Pink
        put("Other", Color.parseColor("#620235"));   // Light Gray
    }};


    private Legend legend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        StatusBarUtils.applyGradientStatusBar(this);

        pieChart = findViewById(R.id.pieChart);
        fetchDocumentCounts();

        barChart = findViewById(R.id.barChart);
        fetchContentCounts();

        lineChart = findViewById(R.id.lineChart);
        fetchUserMetrics();

        donutChart = findViewById(R.id.donutChart);
        fetchGenres();
        donutChart2 = findViewById(R.id.donutChart2);
        fetchMovie_Genres();
        donutChart3 =findViewById(R.id.donutChart3);
        fetchGames_Genres();
        db = FirebaseFirestore.getInstance();


    }

    private void fetchGenres() {
        db.collection("tv_shows").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String genreString = document.getString("genre");
                    if (genreString != null) {
                        classifyGenres(genreString);
                    }
                }
                setUpDonutChart();
            } else {
                Log.e("Firestore", "Error getting documents", task.getException());
            }
        });
    }

    private void fetchMovie_Genres() {
        db.collection("movies").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String genreString = document.getString("Movie_genre");
                    if (genreString != null) {
                        classifyMovieGenres(genreString);
                    }
                }
                setUpMovieDonutChart();
            } else {
                Log.e("Firestore", "Error getting documents", task.getException());
            }
        });
    }

    private void fetchGames_Genres() {
        db.collection("games").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String genreString = document.getString("genre");
                    if (genreString != null) {
                        classifyGameGenres(genreString);
                    }
                }
                setUpGameDonutChart();
            } else {
                Log.e("Firestore", "Error getting documents", task.getException());
            }
        });
    }

    private void classifyGenres(String genreString) {
        String[] genres = genreString.split(", ");

        for (String genre : genres) {
            String mainGenre = extractMainGenre(genre);
            genreCountMapTVShows.put(mainGenre, genreCountMapTVShows.getOrDefault(mainGenre, 0) + 1);
        }
    }

    private void classifyMovieGenres(String genreString) {
        String[] genres = genreString.split(", ");

        for (String genre : genres) {
            String mainGenre = extractMainGenre(genre);
            genreCountMapMovies.put(mainGenre, genreCountMapMovies.getOrDefault(mainGenre, 0) + 1);
        }
    }
    private void classifyGameGenres(String genreString) {
        String[] genres = genreString.split(", ");

        for (String genre : genres) {
            String mainGenre = extractGameMainGenre(genre);
            genreCountMapGames.put(mainGenre, genreCountMapGames.getOrDefault(mainGenre, 0) + 1);
        }
    }

    private String extractMainGenre(String genre) {
        // Group subgenres under main genres
        if (genre.toLowerCase().contains("horror")) return "Horror";
        if (genre.toLowerCase().contains("action")) return "Action";
        if (genre.toLowerCase().contains("drama")) return "Drama";
        if (genre.toLowerCase().contains("comedy")) return "Comedy";
        if (genre.toLowerCase().contains("sci-fi") || genre.toLowerCase().contains("science fiction"))
            return "Sci-Fi";
        if (genre.toLowerCase().contains("thriller")) return "Thriller";
        if (genre.toLowerCase().contains("romance")) return "Romance";
        return "Other"; // If genre doesn't match, categorize as "Other"
    }

    private String extractGameMainGenre(String genre) {
        // Group subgenres under main genres
        if (genre.toLowerCase().contains("racing")) return "Racing";
        if (genre.toLowerCase().contains("action")) return "Action";
        if (genre.toLowerCase().contains("horror")) return "Horror";
        if (genre.toLowerCase().contains("adventure")) return "Adventure";
        if (genre.toLowerCase().contains("shooter video game") || genre.toLowerCase().contains("first-person tactical hero shooter")|| genre.toLowerCase().contains("third-person shooter"))
            return "Shooter";
        return "Other"; // If genre doesn't match, categorize as "Other"
    }

    private void fetchDocumentCounts() {
        db = FirebaseFirestore.getInstance();
        // Get the count of documents in "tv_shows", "games", and "movies" collections
        db.collection("tv_shows").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int tvShowsCount = task.getResult().size();
                        Log.d("Firebase", "TV Shows Count: " + tvShowsCount);

                        db.collection("games").get()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        int gamesCount = task1.getResult().size();
                                        Log.d("Firebase", "Games Count: " + gamesCount);

                                        db.collection("movies").get()
                                                .addOnCompleteListener(task2 -> {
                                                    if (task2.isSuccessful()) {
                                                        int moviesCount = task2.getResult().size();
                                                        Log.d("Firebase", "Movies Count: " + moviesCount);

                                                        // Now set up the PieChart with these values
                                                        setUpPieChart(tvShowsCount, gamesCount, moviesCount);
                                                    }
                                                })
                                                .addOnFailureListener(e -> Log.e("Firebase", "Error getting movies count", e));
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("Firebase", "Error getting games count", e));
                    }
                })
                .addOnFailureListener(e -> Log.e("Firebase", "Error getting tv_shows count", e));
    }

    private void setUpPieChart(int tvShowsCount, int gamesCount, int moviesCount) {
        // Data for PieChart (TV Shows, Movies, Games)
        PieDataSet pieDataSet = new PieDataSet(getPieEntries(tvShowsCount, gamesCount, moviesCount), "Content Breakdown");

        // Set custom colors for the segments
        pieDataSet.setColors(
                ContextCompat.getColor(this, R.color.tv_shows),
                ContextCompat.getColor(this, R.color.Movies),
                ContextCompat.getColor(this, R.color.games)
        );

        // Create PieData object and add dataset
        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // Format as integer (no decimal places)
                return String.format("%d", (int) value);
            }
        });
        pieData.setValueTextSize(16);
        pieData.setValueTextColor(ContextCompat.getColor(this, R.color.white)); // Text color for values

        pieChart.setData(pieData);
        pieChart.animateY(2000, Easing.EaseInCirc);

        // Set up the center text and background color
        pieChart.setCenterText("Content Distribution");
        pieChart.setCenterTextColor(ContextCompat.getColor(this, R.color.white)); // Center text color
        pieChart.setCenterTextSize(16);
        pieChart.setHoleColor(ContextCompat.getColor(this, R.color.black)); // Set black center background

        // Set up the custom Legend
        setUpLegend();

        pieChart.invalidate(); // Refresh chart
    }

    private ArrayList<PieEntry> getPieEntries(int tvShowsCount, int gamesCount, int moviesCount) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(tvShowsCount, "TV Shows"));
        entries.add(new PieEntry(moviesCount, "Movies"));
        entries.add(new PieEntry(gamesCount, "Games"));
        return entries;
    }

    private void setUpLegend() {
        // Custom Legend setup
        ArrayList<LegendEntry> legendEntries = new ArrayList<>();

        legendEntries.add(new LegendEntry(
                "TV Shows",
                Legend.LegendForm.CIRCLE,
                Float.NaN,
                Float.NaN,
                null, ContextCompat.getColor(this, R.color.tv_shows))); // Use ContextCompat

        legendEntries.add(new LegendEntry(
                "Movies",
                Legend.LegendForm.CIRCLE,
                Float.NaN,
                Float.NaN,
                null, ContextCompat.getColor(this, R.color.Movies))); // Use ContextCompat

        legendEntries.add(new LegendEntry(
                "Games",
                Legend.LegendForm.CIRCLE,
                Float.NaN,
                Float.NaN,
                null, ContextCompat.getColor(this, R.color.games))); // Use ContextCompat

        pieChart.getLegend().setCustom(legendEntries);
        pieChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        pieChart.getLegend().setXEntrySpace(35);
        pieChart.getLegend().setTextColor(ContextCompat.getColor(this, R.color.white)); // Use ContextCompat
        pieChart.setDescription(null);
    }

    private void fetchContentCounts() {
        db = FirebaseFirestore.getInstance();

        // Get the count of documents in "New_Trailers", "New_Teasers", and "New_Bloopers"
        db.collection("New_Trailers").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int trailersCount = task.getResult().size();
                        Log.d("Firebase", "Trailers Count: " + trailersCount);

                        db.collection("New_Bloopers").get()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        int bloopersCount = task1.getResult().size();
                                        Log.d("Firebase", "Bloopers Count: " + bloopersCount);

                                        db.collection("New_Teasers").get()
                                                .addOnCompleteListener(task2 -> {
                                                    if (task2.isSuccessful()) {
                                                        int teasersCount = task2.getResult().size();
                                                        Log.d("Firebase", "Teasers Count: " + teasersCount);

                                                        // Now set up the BarChart with these values
                                                        setUpBarChart(trailersCount, bloopersCount, teasersCount);
                                                    }
                                                })
                                                .addOnFailureListener(e -> Log.e("Firebase", "Error getting teasers count", e));
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("Firebase", "Error getting bloopers count", e));
                    }
                })
                .addOnFailureListener(e -> Log.e("Firebase", "Error getting trailers count", e));
    }

    private void setUpBarChart(int trailersCount, int bloopersCount, int teasersCount) {
        // Data for BarChart (Trailers, Bloopers, Teasers)
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, trailersCount)); // Trailers
        entries.add(new BarEntry(1f, bloopersCount)); // Bloopers
        entries.add(new BarEntry(2f, teasersCount)); // Teasers

        // Create a dataset for the BarChart
        BarDataSet barDataSet = new BarDataSet(entries, "Additional Content");

        // Set custom colors for each bar (for Trailers, Bloopers, and Teasers)
        barDataSet.setColors(
                ContextCompat.getColor(this, R.color.tv_shows),  // Trailers color
                ContextCompat.getColor(this, R.color.Movies),    // Bloopers color
                ContextCompat.getColor(this, R.color.games)      // Teasers color
        );
        barDataSet.setValueTextColor(ContextCompat.getColor(this, R.color.white));
        // Create BarData and set it to the BarChart
        BarData barData = new BarData(barDataSet);
        barData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // Format as integer (no decimal places)
                return String.format("%d", (int) value);
            }
        });
        barChart.setData(barData);
        barChart.invalidate(); // Refresh chart

        // Customize BarChart appearance
        barChart.animateY(2000);

        // Add legend for BarChart
        setUpBarLegend();
    }

    private void setUpBarLegend() {
        // Custom Legend setup for BarChart
        ArrayList<LegendEntry> legendEntries = new ArrayList<>();

        // Add legend for Trailers, Bloopers, and Teasers
        legendEntries.add(new LegendEntry(
                "Trailers",
                Legend.LegendForm.SQUARE,
                Float.NaN,
                Float.NaN,
                null, ContextCompat.getColor(this, R.color.tv_shows))); // Trailers color

        legendEntries.add(new LegendEntry(
                "Bloopers",
                Legend.LegendForm.SQUARE,
                Float.NaN,
                Float.NaN,
                null, ContextCompat.getColor(this, R.color.Movies))); // Bloopers color

        legendEntries.add(new LegendEntry(
                "Teasers",
                Legend.LegendForm.SQUARE,
                Float.NaN,
                Float.NaN,
                null, ContextCompat.getColor(this, R.color.games))); // Teasers color

        // Apply the custom legend to the BarChart
        barChart.getLegend().setCustom(legendEntries);
        barChart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        barChart.getLegend().setXEntrySpace(55);
        barChart.getLegend().setTextColor(ContextCompat.getColor(this, R.color.white)); // Text color
    }

    private void fetchUserMetrics() {
        db = FirebaseFirestore.getInstance();

        AtomicInteger activeUserCount = new AtomicInteger();
        AtomicInteger postCount = new AtomicInteger();

        // Fetch active user count from "Profile_user" collection
        db.collection("Profile_user").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        activeUserCount.set(task.getResult().size());
                        Log.d("Firebase", "Active User Count: " + activeUserCount.get());

                        // Fetch post count from "CommunityForum"
                        db.collection("CommunityForum").get()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        postCount.set(task1.getResult().size());
                                        Log.d("Firebase", "Post Count: " + postCount.get());

                                        // Calculate engagement rate (post count used for simplicity)
                                        float engagementRate = postCount.get() == 0 ? 0 : (float) postCount.get() / activeUserCount.get();

                                        // Ensure UI updates are on the main thread
                                        runOnUiThread(() -> setUpLineChart(activeUserCount.get(), engagementRate));
                                    } else {
                                        Log.e("Firebase", "Error getting post count", task1.getException());
                                    }
                                });
                    } else {
                        Log.e("Firebase", "Error getting active user count", task.getException());
                    }
                });
    }

    private void setUpLineChart(int activeUserCount, float engagementRate) {
        Log.d("Debug", "Active User Count: " + activeUserCount);
        Log.d("Debug", "Engagement Rate: " + engagementRate);

        // Convert engagement rate to percentage
        float engagementPercentage = engagementRate * 100;

        // Prepare the data entries for the chart
        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(1f, activeUserCount));   // Active Users
        entries.add(new Entry(2f, engagementPercentage)); // Engagement Rate in percentage

        // ✅ Ensure at least two data points exist before adding to the chart
        if (entries.size() < 2) {
            Log.e("ChartError", "Not enough data for both Active Users and Engagement Rate!");
            return;  // Exit if not enough data
        }

// Create dataset and set properties
        LineDataSet lineDataSet = new LineDataSet(entries, "User Metrics");
        lineDataSet.setLineWidth(2f);
        lineDataSet.setCircleRadius(4f);
        lineDataSet.setColor(ContextCompat.getColor(this, R.color.tv_shows));
        lineDataSet.setCircleColor(ContextCompat.getColor(this, R.color.white));

// Set label (value) text color to white
        lineDataSet.setValueTextColor(ContextCompat.getColor(this, R.color.white));

// Enable fill and set fill color to a purplish shade
        lineDataSet.setDrawFilled(true);
        lineDataSet.setFillColor(ContextCompat.getColor(this, R.color.purple_500));

        // Set data for the chart
        LineData lineData = new LineData(lineDataSet);
        lineData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                // Format as integer (no decimal places)
                return String.format("%d", (int) value);
            }
        });
        lineChart.setData(lineData);
        lineChart.invalidate(); // Refresh the chart
        lineChart.getDescription().setEnabled(false);
    }

    private void setUpDonutChart() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : genreCountMapTVShows.entrySet()) {
            // Only show label if value is big enough
            if (entry.getValue() >= 5) {
                entries.add(new PieEntry(entry.getValue(), entry.getKey())); // Show name only if big enough
            } else {
                entries.add(new PieEntry(entry.getValue())); // Small values: No name
            }

            // Assign color from our custom dark palette
            int color = genreColors.getOrDefault(entry.getKey(), Color.DKGRAY);
            colors.add(color);
        }

        PieDataSet pieDataSet = new PieDataSet(entries, "");
        pieDataSet.setColors(colors);
        pieDataSet.setValueTextSize(14f); // Text size for the value labels
        pieDataSet.setValueTextColor(Color.WHITE); // White text for values outside slices
        pieDataSet.setSliceSpace(5f); // Increase space between slices
        pieDataSet.setSelectionShift(10f); // Highlight effect when tapped

        // Move value labels outside slices
        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setValueLinePart1Length(0.5f); // Length of the first part of the line connecting the value
        pieDataSet.setValueLinePart2Length(0.5f); // Length of the second part of the line
        pieDataSet.setValueLineColor(Color.WHITE); // Color of the line connecting the value

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value); // Show ALL values, even small ones
            }
        });

        donutChart.setHoleRadius(40f);
        donutChart.setTransparentCircleRadius(45f);
        donutChart.setTransparentCircleColor(Color.TRANSPARENT);
        donutChart.setDrawEntryLabels(true);
        donutChart.setDrawHoleEnabled(true);
        donutChart.getDescription().setEnabled(false);
        donutChart.setData(pieData);
        donutChart.setUsePercentValues(false);
        donutChart.setExtraOffsets(10, 10, 10, 10);
        donutChart.invalidate();

        // Customize center hole and add text inside it
        donutChart.setCenterText("TV Show By Genre"); // Text inside the center
        donutChart.setCenterTextColor(Color.WHITE); // White text color for the center text
        donutChart.setCenterTextSize(13f); // Font size for the center text
        donutChart.setHoleColor(ContextCompat.getColor(this, R.color.black));
        donutChart.setCenterTextRadiusPercent(0f); // Ensure the text is centered properly
        donutChart.setCenterTextTypeface(Typeface.DEFAULT_BOLD); // Bold text style for center
        donutChart.getLegend().setTextColor(ContextCompat.getColor(this, R.color.white));

    }

    private void setUpMovieDonutChart() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : genreCountMapMovies.entrySet()) {
            // Only show label if value is big enough
            if (entry.getValue() >= 5) {
                entries.add(new PieEntry(entry.getValue(), entry.getKey())); // Show name only if big enough
            } else {
                entries.add(new PieEntry(entry.getValue())); // Small values: No name
            }

            // Assign color from our custom dark palette
            int color = genreColorsMovies.getOrDefault(entry.getKey(), Color.DKGRAY);
            colors.add(color);
        }

        PieDataSet pieDataSet = new PieDataSet(entries, "");
        pieDataSet.setColors(colors);
        pieDataSet.setValueTextSize(14f); // Text size for the value labels
        pieDataSet.setValueTextColor(Color.WHITE); // White text for values outside slices
        pieDataSet.setSliceSpace(5f); // Increase space between slices
        pieDataSet.setSelectionShift(10f); // Highlight effect when tapped

        // Move value labels outside slices
        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setValueLinePart1Length(0.5f); // Length of the first part of the line connecting the value
        pieDataSet.setValueLinePart2Length(0.5f); // Length of the second part of the line
        pieDataSet.setValueLineColor(Color.WHITE); // Color of the line connecting the value

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value); // Show ALL values, even small ones
            }
        });

        donutChart2.setHoleRadius(40f);
        donutChart2.setTransparentCircleRadius(45f);
        donutChart2.setTransparentCircleColor(Color.TRANSPARENT);
        donutChart2.setDrawEntryLabels(true);
        donutChart2.setDrawHoleEnabled(true);
        donutChart2.getDescription().setEnabled(false);
        donutChart2.setData(pieData);
        donutChart2.setUsePercentValues(false);
        donutChart2.setExtraOffsets(10, 10, 10, 10);
        donutChart2.invalidate();

        // Customize center hole and add text inside it
        donutChart2.setCenterText("Movies By Genre"); // Text inside the center
        donutChart2.setCenterTextColor(Color.WHITE); // White text color for the center text
        donutChart2.setCenterTextSize(13f); // Font size for the center text
        donutChart2.setHoleColor(ContextCompat.getColor(this, R.color.black));
        donutChart2.setCenterTextRadiusPercent(0f); // Ensure the text is centered properly
        donutChart2.setCenterTextTypeface(Typeface.DEFAULT_BOLD); // Bold text style for center
        donutChart2.getLegend().setTextColor(ContextCompat.getColor(this, R.color.white));

    }

    private void setUpGameDonutChart() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : genreCountMapGames.entrySet()) {
            // Only show label if value is big enough
            if (entry.getValue() >= 5) {
                entries.add(new PieEntry(entry.getValue(), entry.getKey())); // Show name only if big enough
            } else {
                entries.add(new PieEntry(entry.getValue())); // Small values: No name
            }

            // Assign color from our custom dark palette
            int color = genreColorsGames.getOrDefault(entry.getKey(), Color.DKGRAY);
            colors.add(color);
        }

        PieDataSet pieDataSet = new PieDataSet(entries, "");
        pieDataSet.setColors(colors);
        pieDataSet.setValueTextSize(14f); // Text size for the value labels
        pieDataSet.setValueTextColor(Color.WHITE); // White text for values outside slices
        pieDataSet.setSliceSpace(5f); // Increase space between slices
        pieDataSet.setSelectionShift(10f); // Highlight effect when tapped

        // Move value labels outside slices
        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setValueLinePart1Length(0.5f); // Length of the first part of the line connecting the value
        pieDataSet.setValueLinePart2Length(0.5f); // Length of the second part of the line
        pieDataSet.setValueLineColor(Color.WHITE); // Color of the line connecting the value

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value); // Show ALL values, even small ones
            }
        });

        donutChart3.setHoleRadius(40f);
        donutChart3.setTransparentCircleRadius(45f);
        donutChart3.setTransparentCircleColor(Color.TRANSPARENT);
        donutChart3.setDrawEntryLabels(true);
        donutChart3.setDrawHoleEnabled(true);
        donutChart3.getDescription().setEnabled(false);
        donutChart3.setData(pieData);
        donutChart3.setUsePercentValues(false);
        donutChart3.setExtraOffsets(10, 10, 10, 10);
        donutChart3.invalidate();

        // Customize center hole and add text inside it
        donutChart3.setCenterText("Games By Genre"); // Text inside the center
        donutChart3.setCenterTextColor(Color.WHITE); // White text color for the center text
        donutChart3.setCenterTextSize(13f); // Font size for the center text
        donutChart3.setHoleColor(ContextCompat.getColor(this, R.color.black));
        donutChart3.setCenterTextRadiusPercent(0f); // Ensure the text is centered properly
        donutChart3.setCenterTextTypeface(Typeface.DEFAULT_BOLD); // Bold text style for center
        donutChart3.getLegend().setTextColor(ContextCompat.getColor(this, R.color.white));

    }

}

