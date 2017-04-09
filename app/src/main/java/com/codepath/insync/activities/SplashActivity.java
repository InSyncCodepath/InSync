package com.codepath.insync.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;

import com.codepath.insync.R;
import com.codepath.insync.models.User;
import com.parse.ParseUser;

public class SplashActivity extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        User currentUser = User.getCurrentUser();
        Intent intent;

        if (currentUser != null) {
             intent = new Intent(SplashActivity.this, EventListActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }

        final Intent splashIntent = intent;
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                startActivity(splashIntent);
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);


    }
}
