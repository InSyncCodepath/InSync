package com.codepath.insync.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.codepath.insync.R;
import com.codepath.insync.models.Event;
import com.parse.ParseGeoPoint;

import java.util.Date;

public class EventCreationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        Event event = new Event("Jane's bridal shower", "707 Continental Cr.", new Date(), new Date(), "Janie is getting married", new ParseGeoPoint(37.3861,122.0839));
        event.saveInBackground();
    }
}
