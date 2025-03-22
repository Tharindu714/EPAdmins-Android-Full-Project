package com.deltacodex.epadmins;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FlingAnimation;

import com.deltacodex.epadmins.model.StatusBarUtils;

import java.lang.reflect.Field;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        StatusBarUtils.applyGradientStatusBar(this);

        TextView textView4 = findViewById(R.id.textView4); // Make sure this ID exists in XML
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            textView4.setVisibility(View.VISIBLE);
            applyStampAnimation(textView4);
        }, 2000);

        // Bounce effect on Y-axis for logo
        ImageView logoImageView = findViewById(R.id.logo_img);
        Keyframe keyframe1 = Keyframe.ofFloat(0f, 0f);
        Keyframe keyframe2 = Keyframe.ofFloat(0.5f, 50f); // Bounce height
        Keyframe keyframe3 = Keyframe.ofFloat(1f, 0f);  // End at original position
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofKeyframe("translationY", keyframe1, keyframe2, keyframe3);
        ObjectAnimator bounceAnimator = ObjectAnimator.ofPropertyValuesHolder(logoImageView, pvhY);
        bounceAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        bounceAnimator.setRepeatMode(ObjectAnimator.REVERSE);
        bounceAnimator.setDuration(1000); // Bounce every second
        bounceAnimator.setInterpolator(new OvershootInterpolator());
        bounceAnimator.start();

        // Text Animation for Event Pulse
        TextView textViewEventPulse = findViewById(R.id.textView2);
        String text = getString(R.string.head);  // Ensure this string exists in strings.xml
        textViewEventPulse.setText(""); // Clear the text initially

        // Simulate typewriter effect for Event Pulse text
        new Handler().postDelayed(new Runnable() {
            int charIndex = 0;

            @Override
            public void run() {
                if (charIndex < text.length()) {
                    textViewEventPulse.append(String.valueOf(text.charAt(charIndex)));
                    charIndex++;
                    new Handler().postDelayed(this, 300); // 300ms delay between characters
                }
            }
        }, 0); // Start immediately

        // Move the slogan from bottom to top
        TextView textViewSlogan = findViewById(R.id.textView3);
        ObjectAnimator bottomToTopAnimator = ObjectAnimator.ofFloat(textViewSlogan, "translationY", 300f, 0f);
        bottomToTopAnimator.setDuration(6000); // 2-second duration for the movement
        bottomToTopAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        bottomToTopAnimator.start();

        // Fling animation and transition to next activity
        new Handler().postDelayed(() -> {
            FlingAnimation flingAnimation = new FlingAnimation(logoImageView, DynamicAnimation.TRANSLATION_X);
            flingAnimation.setStartVelocity(100f);
            flingAnimation.setFriction(1f);
            flingAnimation.start();

            SharedPreferences sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
            boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

            if (isLoggedIn) {
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
            }else{
                Toast.makeText(this, "No User Found : Redirecting to SignUp", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(() -> {
                    Intent intent = new Intent(SplashActivity.this, SignUpActivity.class);
                    startActivity(intent);
                    finish();
                }, 2000); // Hold screen for 5 secs
            }
        }, 5000);
    }

    private void applyStampAnimation(View view) {
        ScaleAnimation stampAnimation = new ScaleAnimation(
                2.5f, 1.0f,  // X-axis scale
                2.5f, 1.0f,  // Y-axis scale
                Animation.RELATIVE_TO_SELF, 0.5f,  // Pivot at center
                Animation.RELATIVE_TO_SELF, 0.5f
        );

        stampAnimation.setDuration(1000);  // Stamp effect speed
        stampAnimation.setInterpolator(new BounceInterpolator()); // Slight bounce effect
        stampAnimation.setRepeatMode(Animation.REVERSE); // Reverse animation on repeat
        stampAnimation.setRepeatCount(Animation.INFINITE); // Infinite loop

        // Fade in effect
        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(1000);
        fadeIn.setRepeatMode(Animation.REVERSE);
        fadeIn.setRepeatCount(Animation.INFINITE); // Loop fade effect too

        // Combining both animations
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(fadeIn);
        animationSet.addAnimation(stampAnimation);

        view.startAnimation(animationSet);
    }

}