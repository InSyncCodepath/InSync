package com.codepath.insync.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.codepath.insync.R;
import com.codepath.insync.databinding.ActivityCreateEventBinding;
import com.codepath.insync.models.Event;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;

import java.io.File;
import java.util.Date;

public class EventCreationActivity extends AppCompatActivity {
    ActivityCreateEventBinding binding;
    String eventName, eventDescription, startDate, startTime, endDate="", endTime="", address="";
    Location location;
    ParseGeoPoint geoPoint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_event);
//        Event event = new Event("Jane's bridal shower", "707 Continental Cr.", new Date(), new Date(), "Janie is getting married", new ParseGeoPoint(37.3861,122.0839));
        eventName = binding.etEventName.getText().toString();
        eventDescription = binding.etDescription.getText().toString();
        startDate = binding.etStartDate.getText().toString();
        startTime = binding.etStartTime.getText().toString();
        endDate = binding.etEndDate.getText().toString();
        endTime = binding.etEndTime.getText().toString();
        //location = binding.etLocation.getText();
        Event event = new Event(eventName, address, new Date(), new Date(), address, new ParseGeoPoint(37.3861,122.0839));

        event.saveInBackground();

    }

    public static Intent newIntent(Activity callingActivity){
        Intent intent = new Intent(callingActivity, EventCreationActivity.class);
        return intent;
    }
}
