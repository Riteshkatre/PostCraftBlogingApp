package com.example.postcraft.Activities;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.postcraft.NetworkResponse.VeriableBag;
import com.example.postcraft.R;
import com.google.firebase.messaging.FirebaseMessaging;

public class SplashActivity extends AppCompatActivity {
    SharedPreference sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);

        setContentView(R.layout.activity_splash);
        ImageView startAlarm=findViewById(R.id.logo);
        sharedPreference=new SharedPreference(SplashActivity.this);
        getFCMToken();


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

    void getFCMToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String token=task.getResult();
                Log.i("token",token);
                sharedPreference.setStringvalue(VeriableBag.Key_Token , token);

            }


        });

    }
    
}