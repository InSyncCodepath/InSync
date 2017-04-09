package com.codepath.insync.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.codepath.insync.R;
import com.codepath.insync.databinding.ActivityCreateEventBinding;
import com.codepath.insync.models.Event;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseGeoPoint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EventCreationActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    ActivityCreateEventBinding binding;
    public final String TAG = EventCreationActivity.class.getName();
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    Calendar eventStartDate = Calendar.getInstance();
    String eventName, eventDescription, endDate = "", endTime = "", address = "";
    EditText location, startTime, startDate;
    ParseGeoPoint geoPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_event);
        startDate = binding.etStartDate;
        startTime = binding.etStartTime;
        location = binding.etLocation;
        Toolbar toolbar = binding.toolbarCreate;
        setSupportActionBar(toolbar);
        location.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    try {
                        Intent intent =
                                new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                        .build(EventCreationActivity.this);
                        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                    } catch (GooglePlayServicesRepairableException e) {
                        // TODO: Handle the error.
                    } catch (GooglePlayServicesNotAvailableException e) {
                        // TODO: Handle the error.
                    }
                }
            }
        });

        startDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(EventCreationActivity.this, date, eventStartDate
                        .get(Calendar.YEAR), eventStartDate.get(Calendar.MONTH),
                        eventStartDate.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(EventCreationActivity.this, EventCreationActivity.this, eventStartDate.get(Calendar.HOUR), eventStartDate.get(Calendar.MINUTE), false).show();
            }
        });
    }


    private void updateDate() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        startDate.setText(sdf.format(eventStartDate.getTime()));
    }

    public static Intent newIntent(Activity callingActivity) {
        Intent intent = new Intent(callingActivity, EventCreationActivity.class);
        return intent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                LatLng latLng = place.getLatLng();
                geoPoint = new ParseGeoPoint(latLng.latitude, latLng.longitude);
                Log.i(TAG, "Place: " + place.getName());
                location.setText(place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            eventStartDate.set(Calendar.YEAR, year);
            eventStartDate.set(Calendar.MONTH, monthOfYear);
            eventStartDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDate();
        }
    };

    private void updateTime() {
        String myFormat = "hh:mm a"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        startTime.setText(sdf.format(eventStartDate.getTime()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event_create, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_create) {
            saveEventDetails();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveEventDetails() {
        eventName = binding.etEventName.getText().toString();
        eventDescription = binding.etDescription.getText().toString();
        endDate = binding.etEndDate.getText().toString();
        endTime = binding.etEndTime.getText().toString();

        Event event = new Event(eventName, location.getText().toString(), eventStartDate.getTime(), eventStartDate.getTime(), eventDescription, geoPoint);
        event.saveInBackground();
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        eventStartDate.set(Calendar.HOUR, hour);
        eventStartDate.set(Calendar.MINUTE, minute);
        updateTime();
    }
}
