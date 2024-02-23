package com.example.greenplate.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.example.greenplate.R;

import com.example.greenplate.views.Home;
import com.example.greenplate.views.LoginView;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        int SPLASH_TIME_OUT = 2000; // Time in milliseconds
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent loginIntent = new Intent(SplashActivity.this, LoginView.class);
                startActivity(loginIntent);
                finish();
            }

        }, SPLASH_TIME_OUT);
    }
}