package com.codepath.insync.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.codepath.insync.R;

public class ContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
    }

    public static Intent newIntent(Activity callingActivity) {
        Intent intent = new Intent(callingActivity, ContactActivity.class);
        return intent;
    }
}
