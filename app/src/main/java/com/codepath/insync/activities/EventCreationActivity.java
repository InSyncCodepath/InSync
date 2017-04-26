package com.codepath.insync.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.gravity.IChildGravityResolver;
import com.beloo.widget.chipslayoutmanager.layouter.breaker.IRowBreaker;
import com.bumptech.glide.Glide;
import com.codepath.insync.Manifest;
import com.codepath.insync.R;
import com.codepath.insync.adapters.InviteeAdapter;
import com.codepath.insync.adapters.SimpleCursorRecyclerAdapterContacts;
import com.codepath.insync.databinding.ActivityCreateEventAnimBinding;
import com.codepath.insync.databinding.ActivityCreateEventBinding;
import com.codepath.insync.models.Contact;
import com.codepath.insync.models.parse.Event;
import com.codepath.insync.models.parse.User;
import com.codepath.insync.models.parse.UserEventRelation;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.R.attr.id;
import static com.codepath.insync.activities.InSyncContactsActivity.PHONE_CONTACTS_REQUEST_CODE;

public class EventCreationActivity extends AppCompatActivity implements SimpleCursorRecyclerAdapterContacts.SimpleCursorAdapterInterface {
    ActivityCreateEventAnimBinding binding;
    public final String TAG = EventCreationActivity.class.getName();
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    Calendar eventStartDate = Calendar.getInstance();
    Calendar eventEndDate = Calendar.getInstance();
    String eventName, eventDescription, address = "";
    EditText location, startTime, startDate, endDate, endTime;
    ImageView setProfileImage, profileImage;
    ParseGeoPoint geoPoint;
    RelativeLayout contactsContainer;
    Button stepOneNext, stepTwoNext, stepSave, addUser;
    public static final int REQUEST_CODE = 1002;
    private static final int REQUEST_CAMERA_ACTIVITY = 1023;
    private static final int REQUEST_CONTACTS = 1;
    RecyclerView inviteeList;
    ArrayList<String> invitees = new ArrayList<>();
    ArrayList<String> insyncInvitees = new ArrayList<>();
    ArrayList<String> chipList = new ArrayList<>();
    private static String[] PERMISSIONS_CONTACT = {Manifest.permission.READ_CONTACTS};
    InviteeAdapter adapter;
    ParseFile parseFile;
    private static final int SELECT_PICTURE = 1025;
    public static final int PHONE_CONTACTS_REQUEST_CODE = 1026;
    private String selectedImagePath;
    CardView eventNameCard, eventDetailCard, eventPeopleCard;
    ArrayList<Contact> contactArrayList =new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_event_anim);
        startDate = binding.etStartDate;
        startTime = binding.etStartTime;
        endDate = binding.etEndDate;
        endTime = binding.etEndTime;
        location = binding.etLocation;
        setProfileImage = binding.ivCamera;
        profileImage = binding.profilePic;
        contactsContainer = binding.contactsContainer;
//        btnNext = binding.next;
        stepOneNext = binding.stepOneNext;
        stepTwoNext = binding.stepTwoNext;
//        stepSave = binding.stepSave;
        addUser = binding.btnAddUser;
        Toolbar toolbar = binding.toolbarCreate;
        setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>"+"Create"+"</font>"));

        inviteeList = binding.inviteeList;
        eventNameCard = binding.eventNameCard;
        eventDetailCard = binding.eventTimeDetails;
        eventPeopleCard = binding.eventAddFriends;

//        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.HORIZONTAL);
//        inviteeList.setLayoutManager(staggeredGridLayoutManager);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        inviteeList.setLayoutManager(linearLayoutManager);
        adapter = new InviteeAdapter(this, invitees);
        inviteeList.setAdapter(adapter);

//        btnNext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(binding.etEventName.getVisibility()==View.VISIBLE){
//                    if (binding.etEventName.getText().equals("")) {
//                        Toast.makeText(EventCreationActivity.this, "Event Name can not be blank", Toast.LENGTH_LONG).show();
//                    } else {
//                        binding.etEventName.setVisibility(View.GONE);
////                        binding.ivCamera.setVisibility(View.VISIBLE);
//                    }
//                }
//            }
//        });

        final Animation animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fadein);
        final Animation animationFadeOut = AnimationUtils.loadAnimation(this, R.anim.fadeout);

        stepOneNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventName = binding.etEventName.getText().toString();
                eventDescription = binding.etDescription.getText().toString();
                eventNameCard.startAnimation(animationFadeOut);
                eventNameCard.setVisibility(View.GONE);
                eventDetailCard.startAnimation(animationFadeIn);
                eventDetailCard.setVisibility(View.VISIBLE);
            }
        });

        stepTwoNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventDetailCard.setAnimation(animationFadeOut);
                eventDetailCard.setVisibility(View.GONE);

                eventPeopleCard.setAnimation(animationFadeIn);
                eventPeopleCard.setVisibility(View.VISIBLE);

            }
        });

//oncrete

        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openContactDialog();
            }
        });

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
//        findViewById(R.id.ivAttach)
//                .setOnClickListener(new View.OnClickListener() {
//
//                    public void onClick(View arg0) {
//
//                        // in onCreate or any event where your want the user to
//                        // select a file
//                        Intent intent = new Intent();
//                        intent.setType("image/*");
//                        intent.setAction(Intent.ACTION_GET_CONTENT);
//                        startActivityForResult(Intent.createChooser(intent,
//                                "Select Picture"), SELECT_PICTURE);
//                    }
//                });




        startDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(EventCreationActivity.this, startDateListener, eventStartDate
                        .get(Calendar.YEAR), eventStartDate.get(Calendar.MONTH),
                        eventStartDate.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(EventCreationActivity.this, startTimeListener, eventStartDate.get(Calendar.HOUR), eventStartDate.get(Calendar.MINUTE), false).show();
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(EventCreationActivity.this, endDateListener, eventEndDate
                        .get(Calendar.YEAR), eventEndDate.get(Calendar.MONTH),
                        eventEndDate.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(EventCreationActivity.this, endTimeListener, eventEndDate.get(Calendar.HOUR), eventEndDate.get(Calendar.MINUTE), false).show();
            }
        });

        //inviteeList = binding.inviteeList;
        setProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(EventCreationActivity.this, "Click me", Toast.LENGTH_LONG).show();
//                setProfileImage.setVisibility(View.GONE);
//                Intent intent = new Intent(EventCreationActivity.this, CameraActivity.class);
//                intent.putExtra("setProfileImage", true);
//                startActivityForResult(intent, 1023);
                openDialog();
            }
        });


    }



    private void updateDate(EditText etDate, Date date) {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        etDate.setText(sdf.format(date));
    }

    public static Intent newIntent(Activity callingActivity) {
        Intent intent = new Intent(callingActivity, EventCreationActivity.class);
        return intent;
    }

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
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
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
            }
        }
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
//                selectedImagePath = getPath(selectedImageUri);
//                selectedImagePath = selectedImageUri.getPath();
                InputStream iStream = null;
                byte[] inputData = null;
                try {
                    iStream = getContentResolver().openInputStream(selectedImageUri);
                    inputData = getBytes(iStream);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                File file = new File(String.valueOf(selectedImageUri));
                Glide.with(EventCreationActivity.this).load(selectedImageUri).into(profileImage);
                profileImage.setVisibility(View.VISIBLE);
                parseFile = new ParseFile(inputData);
            }
        }
        if(requestCode == PHONE_CONTACTS_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                super.onActivityResult(requestCode, resultCode, data);
                Contact contact = Parcels.unwrap(data.getParcelableExtra("result"));
//                for(int i = 0; i< contactArrayList.size(); i++){
//                    invitees.add(contactArrayList.get(i).getName());
//                }
                //invitees.add(contact.getName());
                adapter.notifyDataSetChanged();
            }
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }


    private void showGuests() {
        for (int i = 0; i < chipList.size(); i++) {
            if (!(invitees.contains(chipList.get(i)))) {
                invitees.add(chipList.get(i));
                insyncInvitees.add(chipList.get(i));
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

    DatePickerDialog.OnDateSetListener endDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            eventEndDate.set(Calendar.YEAR, year);
            eventEndDate.set(Calendar.MONTH, monthOfYear);
            eventEndDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDate(endDate, eventEndDate.getTime());
        }
    };

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
        }
    };

    private void updateTime(EditText etTime, Date date) {
        String myFormat = "hh:mm a"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        etTime.setText(sdf.format(date));
        if (etTime == startTime) {
            Date eventEndTime = eventStartDate.getTime();
            eventEndTime.setHours(eventEndTime.getHours() + 3);
            endTime.setText(sdf.format(eventEndTime));
            updateDate(endDate, eventEndTime);
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
        if (id == R.id.action_create) {
            saveEventDetails();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveEventDetails() {
        if (eventDescription.equals("")) {
            Toast.makeText(EventCreationActivity.this, "Event Description can not be blank", Toast.LENGTH_LONG).show();
        } else if (startDate.getText().equals("")) {
            Toast.makeText(EventCreationActivity.this, "Event Date can not be blank", Toast.LENGTH_LONG).show();
        } else if (startTime.getText().equals("")) {
            Toast.makeText(EventCreationActivity.this, "Event Time can not be blank", Toast.LENGTH_LONG).show();
        } else if (invitees.size()==0) {
            Toast.makeText(EventCreationActivity.this, "Oops! Looks like you forgot to add guests", Toast.LENGTH_LONG).show();
        } else  {
//        final Event event = new Event(eventName, location.getText().toString(), eventStartDate.getTime(), eventStartDate.getTime(), eventDescription, geoPoint);
            final Event event = new Event(eventName, location.getText().toString(), eventStartDate.getTime(), eventStartDate.getTime(), eventDescription, geoPoint, parseFile, null);
            event.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    Log.d("Debug", event.getObjectId());
                    final List<String> userIds = new ArrayList<>();
                    for (int i = 0; i < invitees.size(); i++) {
                        User user = null;
                        try {
                            user = User.getUser(invitees.get(i));
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
//                    ParseObject userEvent = ParseObject.create("userEventRelation");
//                    userEvent.put();
                        //userEvent.newUserEventRelation(event, user, false, true, true, true, 4);
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

//                    userEvent.saveInBackground(new SaveCallback() {
//                        @Override
//                        public void done(ParseException e) {
//                            Log.d("Debug", userEvent.getObjectId()+" Object id");
//                            Log.d("Debug", "Event id=" + userEvent.getEventPointerKey() + "USer id" + userEvent.getUserPointerKey());
//
//                        }
//                    });
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
            setResult(RESULT_OK);

            finish();
        }
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
        Intent contactActivityIntent = InSyncContactsActivity.newIntent(this);

        this.startActivityForResult(contactActivityIntent, REQUEST_CODE);
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
    public String getPath(Uri uri) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(projection[0]);
            String path = cursor.getString(columnIndex);
            cursor.close();
            //yourSelectedImage = BitmapFactory.decodeFile(filePath);
            return path;
        }
        // this is our fallback here
        return uri.getPath();
    }

    @Override
    public void showInvitees() {

    }

    private void openDialog() {
        View view = getLayoutInflater().inflate(R.layout.sheet_main, null);
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
        View view = getLayoutInflater().inflate(R.layout.sheet_contact, null);
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
                Intent intent = new Intent(EventCreationActivity.this, ContactActivity.class);
                startActivityForResult(intent, PHONE_CONTACTS_REQUEST_CODE);
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}