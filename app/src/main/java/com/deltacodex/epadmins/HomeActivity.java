package com.deltacodex.epadmins;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.deltacodex.epadmins.Utils.NetworkUtils;
import com.deltacodex.epadmins.model.StatusBarUtils;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.deltacodex.epadmins.databinding.ActivityHomeBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class HomeActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "channel1";
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        checkServerStatus();

        setSupportActionBar(binding.appBarHome.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_upload_Tvshows, R.id.nav_upload_movies, R.id.nav_upload_games,
        R.id.nav_update_Tvshows, R.id.nav_update_movies, R.id.nav_update_games,
                R.id.nav_news_update, R.id.nav_promotional_Updates, R.id.nav_com,
                R.id.nav_Block_user, R.id.nav_Block_product, R.id.nav_bug_report)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            // Handle different navigation items here
            if (id == R.id.nav_logout) {
                showLogoutDialog();
            } else if (id == R.id.nav_SignUp) {
                showRegisterDialog();
            } else if (id == R.id.nav_charts) {
                Intent intent = new Intent(HomeActivity.this, ChartActivity.class);
                startActivity(intent);
            }else if (id == R.id.nav_upload_Tvshows) {
                navController.navigate(R.id.nav_upload_Tvshows);
            } else if (id == R.id.nav_upload_movies) {
                navController.navigate(R.id.nav_upload_movies);
            } else if (id == R.id.nav_upload_games) {
                navController.navigate(R.id.nav_upload_games);
            } else if (id == R.id.nav_update_Tvshows) {
                navController.navigate(R.id.nav_update_Tvshows);
            } else if (id == R.id.nav_update_movies) {
                navController.navigate(R.id.nav_update_movies);
            } else if (id == R.id.nav_update_games) {
                navController.navigate(R.id.nav_update_games);
            } else if (id == R.id.nav_news_update) {
                navController.navigate(R.id.nav_news_update);
            } else if (id == R.id.nav_promotional_Updates) {
                navController.navigate(R.id.nav_promotional_Updates);
            } else if (id == R.id.nav_com) {
                navController.navigate(R.id.nav_com);
            } else if (id == R.id.nav_Block_user) {
                navController.navigate(R.id.nav_Block_user);
            } else if (id == R.id.nav_Block_product) {
                navController.navigate(R.id.nav_Block_product);
            } else if (id == R.id.nav_bug_report) {
                navController.navigate(R.id.nav_bug_report);
            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });

        // Open the drawer automatically when HomeActivity starts
        new Handler().postDelayed(() -> binding.drawerLayout.openDrawer(GravityCompat.START), 500);


        // Get the header of the NavigationView and set user details
        View headerView = navigationView.getHeaderView(0);

        TextView nameTextView = headerView.findViewById(R.id.Name_textView);
        TextView emailTextView = headerView.findViewById(R.id.email_TextView);

        // Retrieve user data from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        String userName = sharedPreferences.getString("userName", "Event Pulse");
        String userEmail = sharedPreferences.getString("userEmail", "Never Miss a Premiere");
        boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

        // Log to confirm email retrieval
        Log.i("EventPulse-Log", "userName retrieved: " + userName);
        Log.i("EventPulse-Log", "userEmail retrieved: " + userEmail);

        // Set the name and email to the respective TextViews
        nameTextView.setText(userName);
        emailTextView.setText(userEmail);

        // Retrieve menu and menu items
        Menu menu = navigationView.getMenu();
        MenuItem logoutItem = menu.findItem(R.id.nav_logout); // Correctly get the logout item
        MenuItem signUpItem = menu.findItem(R.id.nav_SignUp); // Correctly get the signup item
        MenuItem bugReport = menu.findItem(R.id.nav_bug_report);

        boolean visible_toMe = isLoggedIn && "Tharindu Chanaka".equals(userName) && "tharinduchanaka6@gmail.com".equals(userEmail);
        bugReport.setVisible(visible_toMe);

        // Hide the logout option if the user is not logged in
        if (isLoggedIn) {
            logoutItem.setVisible(true);
            signUpItem.setVisible(false);
        } else {
            logoutItem.setVisible(false);
            signUpItem.setVisible(true);
        }
        StatusBarUtils.applyGradientStatusBar(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            showLogoutDialog();
            return true;  // Indicate we handled this item
        } else if (id == R.id.nav_SignUp) {
            showRegisterDialog();
            return true;  // Indicate we handled this item
        } else if (id == R.id.nav_charts) {
            Intent intent = new Intent(HomeActivity.this, ChartActivity.class);
            startActivity(intent);
            return true;  // Indicate we handled this item
        } else if (id == R.id.action_settings) { // Handle EventPulse App Launch
            Intent launchIntent = new Intent();
            launchIntent.setComponent(new ComponentName("com.deltacodex.EventPulse", "com.deltacodex.EventPulse.First_Impression_Activity"));
            startActivity(launchIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void showLogoutDialog() {
        // Ensure dialog context is correct
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);  // Explicit context
        builder.setMessage("Are you sure you want to log out?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, id) -> {
                    // Get NotificationManager instance
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    // Ensure notificationManager is not null
                    if (notificationManager != null) {
                        // Show notification first
                        Notification notification = new NotificationCompat.Builder(HomeActivity.this, CHANNEL_ID)
                                .setSmallIcon(R.drawable.splash)
                                .setContentTitle("EPAdmins Alert")
                                .setContentText("Admin Logged out Successfully")
                                .setPriority(NotificationCompat.PRIORITY_HIGH)  // Ensure visibility
                                .setDefaults(Notification.DEFAULT_ALL)  // Enable sound, vibration, lights
                                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.premiere_wave_studio_inc))
                                .setStyle(
                                        new NotificationCompat.BigPictureStyle()
                                                .bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.premiere_wave_studio_inc))
                                                .setSummaryText("Admin Logged out Successfully")
                                )
                                .build();

                        notificationManager.notify(1, notification);  // Send the notification
                    } else {
                        // Handle the case where notificationManager is null
                        Log.e("NotificationError", "NotificationManager is null.");
                    }

                    // Delay logout to ensure alert shows properly
                    // Now safely log out
                    new Handler().postDelayed(this::logoutUser, 500); // Add a slight delay
                })
                .setNegativeButton("No", (dialog, id) -> dialog.dismiss());

        // Create and show the dialog
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showRegisterDialog() {
        // Create the alert dialog
        new AlertDialog.Builder(HomeActivity.this)
                .setTitle("Register New Member")
                .setMessage("Are you sure you want to go to the registration page?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // If the user confirms, go to the SignUpActivity
                        Intent intent = new Intent(HomeActivity.this, SignUpActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", null)  // Just dismiss if "No" is clicked
                .show();
    }

    private void logoutUser() {
        // Clear SharedPreferences and navigate to First_Impression_Activity
        SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(HomeActivity.this, SignInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu); // Ensure this points to the correct XML
        MenuItem menuItem = menu.findItem(R.id.action_settings);

        if (menuItem != null) {
            SpannableString s = new SpannableString(menuItem.getTitle());
            s.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.white)), 0, s.length(), 0);
            menuItem.setTitle(s);
        } else {
            Log.e("MenuError", "MenuItem is null! Check the ID.");
        }

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void checkServerStatus() {
        new Thread(() -> {
            boolean isInternetWorking = NetworkUtils.isInternetAvailable();
            boolean isFirestoreWorking = NetworkUtils.isFirestoreAvailable();

            runOnUiThread(() -> {
                if (isInternetWorking) {
                    Toast.makeText(this, "📡 Internet is working ✅", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "⚠️ No Internet Connection ❌", Toast.LENGTH_SHORT).show();
                }

                if (isFirestoreWorking) {
                    Toast.makeText(this, "🔥 Fire Store Database is running ✅", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "❌ Fire Store is down!", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}