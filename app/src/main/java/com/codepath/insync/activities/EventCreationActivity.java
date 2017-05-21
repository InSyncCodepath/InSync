package com.codepath.insync.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bumptech.glide.Glide;
import com.codepath.insync.Manifest;
import com.codepath.insync.R;
import com.codepath.insync.adapters.InviteeAdapter;
import com.codepath.insync.adapters.SimpleCursorRecyclerAdapterContacts;
import com.codepath.insync.databinding.ActivityEventCreationBinding;
import com.codepath.insync.models.Contact;
import com.codepath.insync.models.parse.Event;
import com.codepath.insync.models.parse.User;
import com.codepath.insync.models.parse.UserEventRelation;
import com.codepath.insync.utils.Camera;
import com.codepath.insync.utils.CommonUtil;
import com.codepath.insync.utils.Constants;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class EventCreationActivity extends AppCompatActivity implements SimpleCursorRecyclerAdapterContacts.SimpleCursorAdapterInterface {
    ActivityEventCreationBinding binding;
    public final String TAG = EventCreationActivity.class.getName();
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    Calendar eventStartDate = Calendar.getInstance();
    Calendar eventEndDate = Calendar.getInstance();
    String eventName, eventDescription;
    EditText eventTitle;
    TextView startTime, startDate, endDate, endTime, addUser, setProfileImage, location;
    ImageView profileImage;
    ParseGeoPoint geoPoint;
    RelativeLayout contactsContainer;
    //Next buttons
    public static final int REQUEST_CODE = 1002;
    private static final int REQUEST_CAMERA_ACTIVITY = 1023;
    private static final int REQUEST_CONTACTS = 1;
    RecyclerView inviteeList;
    ArrayList<String> invitees = new ArrayList<>();
    ArrayList<String> chipList = new ArrayList<>();
    private static String[] PERMISSIONS_CONTACT = {Manifest.permission.READ_CONTACTS};
    InviteeAdapter adapter;
    ParseFile parseFile;
    private static final int SELECT_PICTURE = 1025;
    public static final int PHONE_CONTACTS_REQUEST_CODE = 1026;
    boolean eventPicSelected = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_event_creation);
        startDate = binding.etStartDate;
        startTime = binding.etStartTime;
        endDate = binding.etEndDate;
        endTime = binding.etEndTime;
        location = binding.tvLocation;
        setProfileImage = binding.tvAttach;
        profileImage = binding.profilePic;
        contactsContainer = binding.contactsContainer;
        Toolbar toolbar = binding.toolbarCreate;
        setSupportActionBar(toolbar);
        addUser = binding.tvInvite;
        eventTitle = binding.eventName;
        inviteeList = binding.inviteeList;
        setupUI(binding.contactsContainer);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        inviteeList.setLayoutManager(linearLayoutManager);
        adapter = new InviteeAdapter(this, invitees);
        inviteeList.setAdapter(adapter);

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(EventCreationActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });

        startDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(EventCreationActivity.this, R.style.EventDateTimePickerStyle, startDateListener, eventStartDate
                        .get(Calendar.YEAR), eventStartDate.get(Calendar.MONTH),
                        eventStartDate.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(eventStartDate.getTime().getTime());

                datePickerDialog.show();
            }
        });

        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new TimePickerDialog(EventCreationActivity.this, R.style.EventDateTimePickerStyle, startTimeListener, eventStartDate.get(Calendar.HOUR), eventStartDate.get(Calendar.MINUTE), false).show();
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(EventCreationActivity.this, R.style.EventDateTimePickerStyle, endDateListener, eventEndDate
                        .get(Calendar.YEAR), eventEndDate.get(Calendar.MONTH),
                        eventEndDate.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMinDate(eventEndDate.getTime().getTime());
                datePickerDialog.show();
            }
        });

        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(EventCreationActivity.this, R.style.EventDateTimePickerStyle, endTimeListener, eventEndDate.get(Calendar.HOUR), eventEndDate.get(Calendar.MINUTE), false).show();
            }
        });

        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openContactDialog();
            }
        });
        setProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openProfileDialog();
            }
        });

        binding.done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveEventDetails();
            }
        });

        binding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        }

        );
    }


    private void updateDate(TextView etDate, Date date) {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        etDate.setText(sdf.format(date));
    }

    public static Intent newIntent(Activity callingActivity) {
        return new Intent(callingActivity, EventCreationActivity.class);
    }

    private void showGuests() {
        for (int i = 0; i < chipList.size(); i++) {
            if (!(invitees.contains(chipList.get(i)))) {
                invitees.add(chipList.get(i));
            }
        }
        adapter.notifyDataSetChanged();
    }

    DatePickerDialog.OnDateSetListener startDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            eventStartDate.set(Calendar.YEAR, year);
            eventStartDate.set(Calendar.MONTH, monthOfYear);
            eventStartDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDate(startDate, eventStartDate.getTime());
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
            }
        }
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                chipList = data.getStringArrayListExtra("result");
                showGuests();
                Log.d("INVITE", invitees.toString());
            } else {
                Log.d("INVITE", "Error");
            }
        }
        if (requestCode == REQUEST_CAMERA_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                String filePath = data.getStringExtra("filePath");
//                Uri newUri = Uri.parse(filePath);
                File file = new File(filePath);
//                String tempPath = "/cache/IMG_20170416_110438.jpg";
//                File tempfile = new File(tempPath);
                parseFile = new ParseFile(file);

                Glide.with(EventCreationActivity.this).load(file).into(profileImage);
                profileImage.setVisibility(View.VISIBLE);
                eventPicSelected = true;
            }
        }

        if(requestCode == PHONE_CONTACTS_REQUEST_CODE){
            if (resultCode == RESULT_OK) {
                ArrayList<Contact> contacts = Parcels.unwrap(data.getParcelableExtra("result"));
                contacts.size();
                //sendMessage(contacts);
            }
        }

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {

                Glide.with(EventCreationActivity.this).load(data.getData()).into(profileImage);
                profileImage.setVisibility(View.VISIBLE);

                Uri selectedImageUri = data.getData();
                try {
                    parseFile = new ParseFile(Camera.readBytes(this, selectedImageUri));
                    eventPicSelected = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendMessage(ArrayList<Contact> guestList) {
        for(int i = 0; i <guestList.size(); i++) {
            String phoneNumber = guestList.get(i).getPhoneNumber();
            CommonUtil.sendInviteLink(phoneNumber, "zV1YKPVe6F");
        }
    }

    DatePickerDialog.OnDateSetListener endDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            eventEndDate.set(Calendar.YEAR, year);
            eventEndDate.set(Calendar.MONTH, monthOfYear);
            eventEndDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDate(endDate, eventEndDate.getTime());
            verifyEndDateTime();
    }
    };

    private void verifyEndDateTime() {
        if(eventEndDate.getTime().getTime() < eventStartDate.getTime().getTime()){
            AlertDialog.Builder builder = new AlertDialog.Builder(new android.view.ContextThemeWrapper(EventCreationActivity.this, R.style.CustomAlertDialog));
            builder
                    .setMessage("Event cannot end before it begins")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            endDate.setText(getResources().getString(R.string.enter_end_date));
                            endTime.setText(getResources().getString(R.string.enter_end_time));
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = builder.create();

            // show it
            alertDialog.show();
        }
    }

    TimePickerDialog.OnTimeSetListener startTimeListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            eventStartDate.set(Calendar.HOUR, hour);
            eventStartDate.set(Calendar.MINUTE, minute);
            updateTime(startTime, eventStartDate.getTime());
        }
    };

    TimePickerDialog.OnTimeSetListener endTimeListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            eventEndDate.set(Calendar.HOUR, hour);
            eventEndDate.set(Calendar.MINUTE, minute);
            updateTime(endTime, eventEndDate.getTime());
            verifyEndDateTime();
        }

    };

    private void updateTime(TextView etTime, Date date) {
        String myFormat = "hh:mm a"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        etTime.setText(sdf.format(date));
        if (etTime == startTime) {
            Calendar c = Calendar.getInstance();
            c.setTime(eventStartDate.getTime());
            c.add(Calendar.HOUR_OF_DAY, 3);
            c.getTime();
            endTime.setText(sdf.format(c.getTime()));
            updateDate(endDate, c.getTime());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_event_create, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.done) {
            saveEventDetails();
            return true;
        }
        //showContacts();
        return id == R.id.cancel || super.onOptionsItemSelected(item);

    }

    private void saveEventDetails() {
        eventName = eventTitle.getText().toString();
        eventDescription = binding.etDetails.getText().toString();
        if (!eventPicSelected) {
            CommonUtil.createSnackbar(contactsContainer, EventCreationActivity.this, "Please Enter an image for the Event");
        }
        if (eventName.equals("")) {
            CommonUtil.createSnackbar(contactsContainer, EventCreationActivity.this, "Please Enter Event Name");
        } else if (startDate.getText().equals("Enter start date")) {
            CommonUtil.createSnackbar(contactsContainer, EventCreationActivity.this, "Please Enter Event Start Date");
        } else if (startTime.getText().equals("Enter start time")) {
            CommonUtil.createSnackbar(contactsContainer, EventCreationActivity.this, "Please Enter Event Start Time");
        } else if (location.getText().equals("")) {
            CommonUtil.createSnackbar(contactsContainer, EventCreationActivity.this, "Please Enter Event Location");
        } else if (eventDescription.equals("")) {
            CommonUtil.createSnackbar(contactsContainer, EventCreationActivity.this, "Please Enter Event Description");
        } else if (invitees.size() == 0) {
            CommonUtil.createSnackbar(contactsContainer, EventCreationActivity.this, "Please add Invitees");
        } else {
            parseFile.saveInBackground(new SaveCallback() {
                public void done(ParseException e) {
                    // If successful add file to user and signUpInBackground
                    if (null == e)
                        savetoParse();
                }
            });
//
            setResult(RESULT_OK);

            finish();
        }
    }

    private void savetoParse() {
        final Event event = new Event(eventName, location.getText().toString(), eventStartDate.getTime(), eventStartDate.getTime(), eventDescription, geoPoint, parseFile, null);
        event.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.d("Debug", event.getObjectId());
                final List<String> userIds = new ArrayList<>();
                for (int i = 0; i < invitees.size(); i++) {
                    User user;
                    try {
                        user = User.getUser(invitees.get(i));
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                        continue;
                    }

                    final UserEventRelation userEvent = new UserEventRelation(event, user.getObjectId(), false, true, true, true, 2);
                    userEvent.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            Log.d("Debug", userEvent.getObjectId() + " Object id");
                            Log.d("Debug", "Event id=" + userEvent.getEvent() + " USer id" + userEvent.getUserIdKey());
                            userIds.add(userEvent.getUserId());
                            if (userIds.size() == invitees.size()) {
                                sendInviteNotifcations(event, userIds);
                            }
                        }
                    });

                }

                final UserEventRelation hostEvent = new UserEventRelation(event, User.getCurrentUser().getObjectId(), true, true, true, true, 0);

                hostEvent.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Log.d("Debug", "Event id=" + hostEvent.getEvent() + "USer id" + hostEvent.getUserIdKey());

                    }
                });


            }
        });
        Intent returnIntent = new Intent();
        returnIntent.putExtra("event", event.getName());
        setResult(Activity.RESULT_OK,returnIntent);
        finish();

           }


    public void sendInviteNotifcations(Event event, List<String> userIds) {
        HashMap<String, Object> payload = new HashMap<>();
        HashMap<String, Object> notiInfo = new HashMap<>();
        notiInfo.put("title", "You've been invited!");
        notiInfo.put("text", event.getName());
        notiInfo.put("eventId", event.getObjectId());
        notiInfo.put("notificationType", Constants.NEW_EVENT);
        payload.put("customData", notiInfo);
        payload.put("userIds", userIds);
        ParseCloud.callFunctionInBackground("pushChannelTest", payload, new FunctionCallback<Object>() {

            @Override
            public void done(Object object, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error sending push to cloud: " + e.toString());
                } else {
                    Log.d(TAG, "Push sent successfully!");
                }
            }
        });
    }

    public void showContacts() {
        Log.i(TAG, "Show contacts button pressed. Checking permissions.");

        // Verify that all required contact permissions have been granted.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            // Contacts permissions have not been granted.
            Log.i(TAG, "Contact permissions has NOT been granted. Requesting permissions.");
            requestContactsPermissions();

        } else {
            // Contact permissions have been granted. Show the contacts fragment.
            Log.i(TAG, "Contact permissions have already been granted. Displaying contact details.");
            showContactDetails();
        }
    }


    private void showContactDetails() {
        //Intent contactActivityIntent = ContactActivity.newIntent(this);
//        Intent contactActivityIntent = InSyncContactsActivity.newIntent(this);
//        this.startActivityForResult(contactActivityIntent, REQUEST_CODE);
        Intent intent = new Intent(EventCreationActivity.this, ContactActivity.class);
        startActivityForResult(intent, PHONE_CONTACTS_REQUEST_CODE);
    }

    private void requestContactsPermissions() {
        ActivityCompat.requestPermissions(this, PERMISSIONS_CONTACT, REQUEST_CONTACTS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_ACTIVITY) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission has been granted, preview can be displayed
                Log.i(TAG, "CAMERA permission has now been granted. Showing preview.");
                Snackbar.make(contactsContainer, R.string.permision_available_camera,
                        Snackbar.LENGTH_SHORT).show();
            } else {
                Log.i(TAG, "CAMERA permission was NOT granted.");
                Snackbar.make(contactsContainer, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT).show();

            }

        } else if (requestCode == REQUEST_CONTACTS) {
            Log.i(TAG, "Received response for contact permissions request.");

            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // All required permissions have been granted, display contacts fragment.
                Snackbar.make(contactsContainer, R.string.permision_available_contacts,
                        Snackbar.LENGTH_SHORT)
                        .show();
                showContactDetails();
            } else {
                Log.i(TAG, "Contacts permissions were NOT granted.");
                Snackbar.make(contactsContainer, R.string.permissions_not_granted,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void showInvitees() {

    }

    private void openProfileDialog() {
        View view = getLayoutInflater().inflate(R.layout.sheet_main, binding.svEventCreation, false);
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);
        TextView camera_sel = (TextView) view.findViewById(R.id.bttmCamera);
        TextView gallery_sel = (TextView) view.findViewById(R.id.bttmGallery);
        camera_sel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventCreationActivity.this, CameraActivity.class);
                intent.putExtra("is_profile_pic", true);
                startActivityForResult(intent, 1023);
                dialog.dismiss();
            }
        });
        gallery_sel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), SELECT_PICTURE);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void openContactDialog() {
        View view = getLayoutInflater().inflate(R.layout.sheet_contact, binding.svEventCreation, false);
        final BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);
        TextView insyncContact = (TextView) view.findViewById(R.id.bttmInsynContact);
        TextView phoneContact = (TextView) view.findViewById(R.id.bttmPhoneContacts);
        insyncContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventCreationActivity.this, InSyncContactsActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                dialog.dismiss();
            }
        });
        phoneContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showContacts();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void setupUI(View view) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    binding.eventName.clearFocus();
                    binding.etDetails.clearFocus();
                    return false;
                }
            });
        }
    }

    public void cancel(){
        //AlertDialog.Builder builder = new AlertDialog.Builder(EventCreationActivity.this);
        AlertDialog.Builder builder = new AlertDialog.Builder(new android.view.ContextThemeWrapper(EventCreationActivity.this, R.style.CustomAlertDialog));

        builder.setTitle("Confirm Delete");
        builder
                .setMessage("Are you sure you want to discard this event?")
                .setCancelable(false)
                .setPositiveButton("Discard", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = builder.create();

        // show it
        alertDialog.show();
    }
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            cancel();
        }
        return super.onKeyDown(keyCode, event);
    }
}