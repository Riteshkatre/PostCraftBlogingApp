package com.example.postcraft.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.postcraft.R;

public class MainActivity extends AppCompatActivity {

    CardView cv1;

    TextView tv1;

    private SharedPreference sharedPreference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);

        setContentView(R.layout.activity_main);
        cv1=findViewById(R.id.cv1);
        tv1=findViewById(R.id.tv1);
        sharedPreference = new SharedPreference(this);

        if (sharedPreference.isLoggedIn()) {
            openHomePage();
        }

        cv1.setOnClickListener(v -> {
            Intent i=new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(i);
        });
        tv1.setOnClickListener(v -> {
            Intent i=new Intent(MainActivity.this, LogingActivity.class);
            startActivity(i);
            finish();


        });
    }

    private void openHomePage() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}