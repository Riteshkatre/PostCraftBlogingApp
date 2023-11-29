package com.example.postcraft.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.example.postcraft.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ImageView startAlarm=findViewById(R.id.logo);


        ObjectAnimator rotationAnimator = ObjectAnimator.ofFloat(startAlarm, "rotationY", 0f, 360f);
        rotationAnimator.setDuration(2500);
        rotationAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        rotationAnimator.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, 4000);
    }
    
}