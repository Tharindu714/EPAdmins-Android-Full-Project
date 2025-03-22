package com.deltacodex.epadmins;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.deltacodex.epadmins.SQLite_DB.DatabaseHelper;
import com.deltacodex.epadmins.model.StatusBarUtils;

public class SignInActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "channel1";
    EditText user_NameInput, passwordInput;
    DatabaseHelper databaseHelper;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        StatusBarUtils.applyGradientStatusBar(this);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Channel_NO1",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(notificationChannel);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            startActivity(new Intent(SignInActivity.this, HomeActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_sign_in);
        user_NameInput = findViewById(R.id.username);
        passwordInput = findViewById(R.id.email);
        databaseHelper = new DatabaseHelper(this);

        Button Sign_up_Button = findViewById(R.id.register_1);
        Sign_up_Button.setOnClickListener(view -> {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        Button Sign_in_Button = findViewById(R.id.signIn_1);
        Sign_in_Button.setOnClickListener(view -> {
            String userName = user_NameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (userName.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignInActivity.this, "UserName or Password not Entered", Toast.LENGTH_LONG).show();
            } else {
                boolean isValidUser = databaseHelper.checkUserExists(userName, password);

                if (isValidUser) {
                    // User exists, now get the email
                    String userEmail = databaseHelper.getEmailByUsername(userName); // Fetch email using the username

                    if (userEmail != null) {
                        // Store username and email in SharedPreferences
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isLoggedIn", true);
                        editor.putString("userName", userName);  // Store username
                        editor.putString("userEmail", userEmail);  // Store email fetched from the database
                        editor.apply();

                        Notification notification = new NotificationCompat.Builder(SignInActivity.this, CHANNEL_ID)
                                .setSmallIcon(R.drawable.splash)
                                .setContentTitle("EPAdmins Alert")
                                .setContentText(userName+" Logged in to Event Pulse Account Just Now")
                                .setPriority(NotificationCompat.PRIORITY_HIGH)  // Ensure visibility
                                .setDefaults(Notification.DEFAULT_ALL)  // Enable sound, vibration, lights
                                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.premiere_wave_studio_inc))
                                .setStyle(
                                        new NotificationCompat.BigPictureStyle()
                                                .bigPicture(BitmapFactory.decodeResource(getResources(),R.drawable.premiere_wave_studio_inc))
                                                .setSummaryText(userEmail+" has Logged Now")
                                )
                                .build();

                        notificationManager.notify(1, notification);
                        startActivity(new Intent(SignInActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        Toast.makeText(SignInActivity.this, "Email not found for user!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignInActivity.this, "Invalid Credentials!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
