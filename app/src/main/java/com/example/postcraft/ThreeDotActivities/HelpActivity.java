package com.example.postcraft.ThreeDotActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.View;

import com.example.postcraft.R;

public class HelpActivity extends AppCompatActivity {

    CardView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        back=findViewById(R.id.back);


        back.setOnClickListener(v -> finish());
    }
}