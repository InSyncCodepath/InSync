package com.codepath.insync.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.IdRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.support.v7.graphics.Palette;

import com.codepath.insync.R;
import com.codepath.insync.databinding.ActivityEventDetailBinding;
import com.codepath.insync.fragments.ConfirmationFragment;
import com.codepath.insync.fragments.MessageSendFragment;
import com.codepath.insync.fragments.PastEventDetailFragment;
import com.codepath.insync.fragments.UpcomingEventDetailFragment;
import com.codepath.insync.listeners.OnVideoCreateListener;
import com.codepath.insync.models.parse.Event;
import com.codepath.insync.models.parse.User;
import com.codepath.insync.models.parse.UserEventRelation;
import com.codepath.insync.utils.Constants;
import com.codepath.insync.utils.DateUtil;
import com.codepath.insync.utils.MediaClient;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class EventDetailActivity extends AppCompatActivity implements
        UpcomingEventDetailFragment.OnViewTouchListener,
        ConfirmationFragment.UpdateDraftDialogListener,
        OnVideoCreateListener {
    private static final String TAG = "EventDetailActivity";

    ActivityEventDetailBinding binding;
    CollapsingToolbarLayout collapsingToolbar;
    Event event;
    String eventId;
    boolean isCurrent;
    boolean canTrack;
    int numAttending;
    int numDecline;
    int numPending;
    int currentRbnId;
    MessageSendFragment messageSendFragment;
    UserEventRelation currentUserEvent;
    private boolean firstLoad;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_event_detail);

        fragmentManager = getSupportFragmentManager();
        firstLoad = true;
        processIntent();
        setupToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu for the current event
        if (canTrack) {
            getMenuInflater().inflate(R.menu.menu_event_detail, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void processIntent() {
        Intent intent = getIntent();
        eventId = intent.getStringExtra("eventId");
        isCurrent = intent.getBooleanExtra("isCurrent", false);
        canTrack = intent.getBooleanExtra("canTrack", false);
        Event.findEvent(eventId, new GetCallback<Event>() {
            @Override
            public void done(Event eventObj, ParseException e) {
                if (e == null) {
                    event = eventObj;
                    findAttendance();
                    loadViews();
                    loadFragments();
                } else {
                    event = null;
                    Log.e(TAG, "Error finding event.");
                    finish();
                }
            }
        });

    }

    private void findAttendance() {
        numAttending = 0;
        numDecline = 0;
        numPending = 0;
        UserEventRelation.findAttendees(event, new FindCallback<UserEventRelation>() {
            @Override
            public void done(List<UserEventRelation> userEventRelations, ParseException e) {
                if (e == null) {
                    for (UserEventRelation userEventRelation : userEventRelations) {
                        int rsvpStatus = userEventRelation.getRsvpStatus();
                        switch (rsvpStatus) {
                            case Constants.ATTENDING:
                                numAttending++;
                                break;
                            case Constants.DECLINE:
                                numDecline++;
                                break;
                            default:
                                numPending++;
                                break;
                        }
                        if (userEventRelation.getUserId().equals(User.getCurrentUser().getObjectId())) {
                            currentRbnId = binding.rgEDRsvp.getCheckedRadioButtonId();
                            currentUserEvent = userEventRelation;

                            if (firstLoad) {
                                firstLoad = false;
                                if (rsvpStatus == Constants.ATTENDING) {
                                    binding.rbnAttending.setChecked(true);
                                } else if (rsvpStatus == Constants.DECLINE) {
                                    binding.rbnDecline.setChecked(true);
                                } else {
                                    binding.rbnPending.setChecked(true);
                                }
                                // Disable the radio button selection if the user is hosting
                                if (currentUserEvent.isHosting()) {
                                    binding.rbnAttending.setEnabled(false);
                                    binding.rbnDecline.setEnabled(false);
                                    binding.rbnPending.setEnabled(false);
                                } else {
                                    setupRadioButtonGroup();
                                }

                            }
                        }
                    }
                } else {
                    Log.e(TAG, "Error loading RSVP status: "+e.getLocalizedMessage());
                }
                binding.rbnAttending.setText(numAttending+" "+Constants.ATTENDING_STR);
                binding.rbnDecline.setText(numDecline+" "+Constants.DECLINE_STR);
                binding.rbnPending.setText(numPending+" "+Constants.PENDING_STR);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_track:
                Intent intent = new Intent(EventDetailActivity.this, LocationTrackerActivity.class);
                startActivity(intent);
            case R.id.action_highlights:
                createHighlights();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createHighlights() {
        String message = "Your event will end and you will not be able to post. Do you want to continue creating highlights?";
        ConfirmationFragment confirmationFragment = ConfirmationFragment.newInstance(message, "Continue", "Cancel");
        confirmationFragment.show(fragmentManager, "fragment_confirmation");
    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText || view instanceof FloatingActionButton)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideAndClearFocus(v);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    private void hideAndClearFocus(View v) {
        InputMethodManager imm =(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        messageSendFragment.clearViewFocus();
    }

    private void loadViews() {
        Bitmap bitmap = event.getProfileImageBitmap();
        if (bitmap != null) {
            binding.ivEDProfile.setImageBitmap(bitmap);
        }
        if (!isCurrent) {
            binding.rgEDRsvp.setVisibility(View.GONE);
        }
        binding.tvEDName.setText(event.getName());
        binding.tvEDDescription.setText(event.getDescription());
        binding.tvEDDate.setText(DateUtil.getDateTimeInFormat(event.getStartDate()));
        binding.tvEDLocation.setText(event.getAddress());
    }

    private void setupToolbar() {
        collapsingToolbar = binding.ctlEventDetail;

        setSupportActionBar(binding.tbEventDetail);
        getSupportActionBar().setTitle("");

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.palette);
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @SuppressWarnings("ResourceType")
            @Override
            public void onGenerated(Palette palette) {
                int vibrantColor = palette.getVibrantColor(R.color.primary_light);
                collapsingToolbar.setContentScrimColor(vibrantColor);
                collapsingToolbar.setStatusBarScrimColor(R.color.accent);
            }
        });

        binding.abEventDetail.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                Log.d(TAG, "Appbar offset changed to: "+verticalOffset);
                int vOffSetThreshold = isCurrent ? 700 : 480;
                if (Math.abs(verticalOffset) > vOffSetThreshold) {
                    TextView tvEventName = (TextView) collapsingToolbar.findViewById(R.id.tvEDName);
                    collapsingToolbar.setTitle(tvEventName.getText().toString());
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    binding.rlToolbar.setVisibility(View.INVISIBLE);
                } else {
                    collapsingToolbar.setTitle(Constants.EMPTY_STR);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    binding.rlToolbar.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    private void setupRadioButtonGroup() {
        binding.rgEDRsvp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                // Get current selected radio button and increment count locally
                if (checkedId == binding.rbnAttending.getId()) {
                    currentUserEvent.setRsvpStatusKey(Constants.ATTENDING);
                    numAttending++;
                } else if (checkedId == binding.rbnDecline.getId()) {
                    currentUserEvent.setRsvpStatusKey(Constants.DECLINE);
                    numDecline++;
                } else {
                    currentUserEvent.setRsvpStatusKey(Constants.PENDING);
                    numPending++;
                }

                // Get previously checked radio button and decrement count locally
                if (currentRbnId == binding.rbnAttending.getId()) {
                    numAttending--;
                } else if (currentRbnId == binding.rbnDecline.getId()) {
                    numDecline--;
                } else {
                    numPending--;
                }

                // Update attendance
                binding.rbnAttending.setText(numAttending+" "+Constants.ATTENDING_STR);
                binding.rbnDecline.setText(numDecline+" "+Constants.DECLINE_STR);
                binding.rbnPending.setText(numPending+" "+Constants.PENDING_STR);

                // Update remote store and refresh
                currentUserEvent.updateRsvpStatus(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.d(TAG, "Rsvp status updated successfully.");
                            findAttendance();
                        } else {
                            Log.e(TAG, "Error updating RSVP status: "+e.getLocalizedMessage());
                        }
                    }
                });

            }
        });
    }

    private void loadFragments() {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if (isCurrent) {
            // Load current and upcoming event detail
            UpcomingEventDetailFragment upcomingEventDetailFragment = new UpcomingEventDetailFragment();
            ft.replace(R.id.flMessages, upcomingEventDetailFragment);
            messageSendFragment = new MessageSendFragment();
            ft.replace(R.id.flMessageSend, messageSendFragment);
            setupUI(binding.clED);
        } else {
            PastEventDetailFragment pastEventDetailFragment =
                    PastEventDetailFragment.newInstance(event.getObjectId(), event.getName(), event.getHighlightsVideo());
            ft.replace(R.id.flMessages, pastEventDetailFragment);
        }

        ft.commit();

    }

    @Override
    public void onViewTouch(View v) {
        hideAndClearFocus(v);
    }

    @Override
    public void onConfirmUpdateDialog(int position) {
        if (position == DialogInterface.BUTTON_POSITIVE) {
            event.setEndDate(new Date());
            event.setHasEnded(true);

            final List<ParseFile> edImages = new ArrayList<>();
            ParseQuery<ParseObject> parseQuery = event.getAlbumRelation().getQuery();

            parseQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {

                        for (ParseObject imageObject: objects) {
                            edImages.add(imageObject.getParseFile("image"));
                        }
                    } else {
                        Log.e(TAG, "Error fetching event album");
                    }
                }
            });
            MediaClient mediaClient = new MediaClient(this);
            mediaClient.createHighlights(this, event, edImages, "party");

        }
    }

    @Override
    public void onCreateSuccess(String videoUrl) {
        Log.d(TAG, "Video created successfully. Url: "+videoUrl);
        event.setHighlightsVideo(videoUrl);
        event.updateEvent(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Event highlights have been successfully created and updated");
                    isCurrent = false;
                    canTrack = false;
                    invalidateOptionsMenu();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    ft.remove(messageSendFragment);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
                    ft.commit();
                    loadViews();
                    loadFragments();
                } else {
                    Log.d(TAG, "Event highlights creation failed with error: "+e.getLocalizedMessage());
                }
            }
        });


    }

    @Override
    public void onCreateFailure(int status, String message) {
        Log.e(TAG, "Video could not be created. status: "+status+", message: "+message);

    }
}
