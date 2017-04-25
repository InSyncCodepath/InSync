package com.codepath.insync.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.codepath.insync.Manifest;
import com.codepath.insync.R;
import com.codepath.insync.databinding.ActivityEventDetailBinding;
import com.codepath.insync.fragments.ConfirmationFragment;
import com.codepath.insync.fragments.MessageSendFragment;
import com.codepath.insync.fragments.PastEventDetailFragment;
import com.codepath.insync.fragments.PastEventWaitFragment;
import com.codepath.insync.fragments.UpcomingEventDetailFragment;
import com.codepath.insync.listeners.OnImageClickListener;
import com.codepath.insync.listeners.OnMessageChangeListener;
import com.codepath.insync.models.parse.Event;
import com.codepath.insync.models.parse.Message;
import com.codepath.insync.models.parse.User;
import com.codepath.insync.models.parse.UserEventRelation;
import com.codepath.insync.utils.Camera;
import com.codepath.insync.utils.Constants;
import com.codepath.insync.utils.CommonUtil;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;

import com.parse.ParseFile;
import com.parse.SaveCallback;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;


public class EventDetailActivity extends AppCompatActivity implements
        UpcomingEventDetailFragment.OnViewTouchListener,
        ConfirmationFragment.UpdateDraftDialogListener,
        OnImageClickListener,
        OnMessageChangeListener{

    private static final String TAG = "EventDetailActivity";
    private static final int REQUEST_CAMERA_ACTIVITY = 1027;
    private static final int SELECT_PICTURE = 1028;

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
    PastEventWaitFragment pastEventWaitFragment;
    PastEventDetailFragment pastEventDetailFragment;
    UserEventRelation currentUserEvent;
    private boolean firstLoad;
    FragmentManager fragmentManager;
    RelativeLayout rlEventDetail;
    UpcomingEventDetailFragment upcomingEventDetailFragment;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_event_detail);

        fragmentManager = getSupportFragmentManager();
        firstLoad = true;
        rlEventDetail = binding.rlEventDetail;
        binding.fabEDSend.setVisibility(View.GONE);

        postponeEnterTransition();
        processIntent();
        setupToolbar();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu for the current event
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return super.onCreateOptionsMenu(menu);
        }
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
        binding.ivEDProfile.setTransitionName(intent.getStringExtra("transition_name"));
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

    private void setupChatClickListeners() {
        binding.fabEDSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageSendFragment.setupMessagePosting();
            }
        });
        binding.fabEDCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventDetailActivity.this, CameraActivity.class);
                startActivityForResult(intent, REQUEST_CAMERA_ACTIVITY);
                binding.famEDMedia.toggle(true);
            }
        });

        binding.fabEDGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), SELECT_PICTURE);
                binding.famEDMedia.toggle(true);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA_ACTIVITY && resultCode == RESULT_OK) {
            String message = data.getStringExtra("message");
            String filePath = data.getStringExtra("filePath");
            ParseFile parseFile = null;
            if (filePath != null) {
                File file = new File(filePath);
                parseFile = new ParseFile(file);
            } else {
                try {
                    parseFile = new ParseFile(Camera.readBytes(getApplicationContext(), imageUri));
                    imageUri = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (parseFile != null) {
                messageSendFragment.setupImagePosting(message, parseFile);
            }

        } else if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK) {
            Intent intent = new Intent(EventDetailActivity.this, CameraActivity.class);
            imageUri = data.getData();
            intent.putExtra("image_uri", imageUri.toString());
            startActivityForResult(intent, REQUEST_CAMERA_ACTIVITY);
        }
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
                finish();
                return true;
            case R.id.action_track:
                Intent intent = new Intent(EventDetailActivity.this, LocationTrackerActivity.class);
                intent.putExtra("eventId", event.getObjectId());
                intent.putExtra("eventLatitude", event.getLocation().getLatitude());
                intent.putExtra("eventLongitude", event.getLocation().getLongitude());
                startActivity(intent);
                break;
            case R.id.action_highlights:
                handleHighlightsAction();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleHighlightsAction() {
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
        ParseFile profileImage = event.getProfileImage();
        if (profileImage != null) {
            /*Glide.with(this)
                    .load(profileImage.getUrl())
                    .crossFade()
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            scheduleStartPostponedTransition(binding.ivEDProfile);
                        }
                    });*/
            Picasso.with(this).load(profileImage.getUrl())
                    .placeholder(R.drawable.ic_camera_alt_white_48px)
                    .resize(binding.ivEDProfile.getWidth(), 0)
                    .transform(new RoundedCornersTransformation(10, 10)).into(binding.ivEDProfile,
                    new Callback() {
                        @Override
                        public void onSuccess() {
                            // Call the "scheduleStartPostponedTransition()" method
                            // below when you know for certain that the shared element is
                            // ready for the transition to begin.
                            scheduleStartPostponedTransition(binding.ivEDProfile);
                        }

                        @Override
                        public void onError() {
                            // ...
                        }
                    });

        }
        if (!isCurrent) {
            binding.rgEDRsvp.setVisibility(View.GONE);
        }
        binding.tvEDName.setText(event.getName());
        binding.tvEDDescription.setText(event.getDescription());
        binding.tvEDDate.setText(CommonUtil.getDateTimeInFormat(event.getStartDate()));
        binding.tvEDLocation.setText(event.getAddress());
    }

    private void scheduleStartPostponedTransition(final View sharedElement) {
        sharedElement.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        binding.ivEDProfile.getViewTreeObserver().removeOnPreDrawListener(this);
                        supportStartPostponedEnterTransition();
                        return true;
                    }
                }
        );
    }

    private void setupToolbar() {

        collapsingToolbar = binding.ctlEventDetail;

        setSupportActionBar(binding.tbEventDetail);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
        }


        /*Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.palette);
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @SuppressWarnings("ResourceType")
            @Override
            public void onGenerated(Palette palette) {
                int vibrantColor = palette.getVibrantColor(R.color.primary_light);
                collapsingToolbar.setContentScrimColor(vibrantColor);
                collapsingToolbar.setStatusBarScrimColor(R.color.accent);
            }
        });*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            collapsingToolbar.setContentScrimColor(getColor(R.color.light_green));
            collapsingToolbar.setStatusBarScrimColor(getColor(R.color.accent));
        }


        binding.abEventDetail.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                Log.d(TAG, "Appbar offset changed to: "+verticalOffset);
                // Calculate ActionBar height
                int vOffSetThreshold = isCurrent ? 520 : 350;
                if (Math.abs(verticalOffset) > vOffSetThreshold) {
                    TextView tvEventName = (TextView) collapsingToolbar.findViewById(R.id.tvEDName);
                    collapsingToolbar.setTitle(tvEventName.getText().toString());
                    collapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.primary_text));
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    binding.rlToolbar.setVisibility(View.INVISIBLE);
                    Drawable toolbarDrawable = ResourcesCompat.getDrawable(getResources(),
                            R.drawable.theme_gradient, null);
                    collapsingToolbar.setBackground(toolbarDrawable);
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
            upcomingEventDetailFragment = UpcomingEventDetailFragment.newInstance(eventId);
            ft.replace(R.id.flMessages, upcomingEventDetailFragment);
            messageSendFragment = MessageSendFragment.newInstance(eventId);
            ft.replace(R.id.flMessageSend, messageSendFragment);
            setupUI(binding.clED);
            setupChatClickListeners();
        } else {
            binding.flMessageSend.setVisibility(View.GONE);
            binding.famEDMedia.setVisibility(View.GONE);
            pastEventDetailFragment =
                    PastEventDetailFragment.newInstance(event.getObjectId(), event.getName(), event.getTheme());
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

            event.setHasEnded(true);
            isCurrent = false;
            canTrack = false;
            invalidateOptionsMenu();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.remove(messageSendFragment);
            binding.flMessageSend.setVisibility(View.GONE);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            pastEventWaitFragment = PastEventWaitFragment.newInstance(true, null, -1);
            ft.replace(R.id.flMessages, pastEventWaitFragment);
            ft.commit();
            CommonUtil.createSnackbar(rlEventDetail, this, "Your event has ended. The highlights are being created!");
            event.updateEvent(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.d(TAG, "Past event status updated successfully!");
                        loadViews();
                        loadFragments();
                    } else {
                        Log.e(TAG, "Could not update past event flag");
                    }
                }
            });
        }
    }

    @Override
    public void onItemClick(ArrayList<String> images, int position) {
        Transition changeTransform = TransitionInflater.from(this).
                inflateTransition(R.transition.change_image_transform);
        binding.flMessageSend.setVisibility(View.GONE);
        Transition explodeTransform = TransitionInflater.from(this).inflateTransition(android.R.transition.explode);
        Transition slideBottom = TransitionInflater.from(this).inflateTransition(android.R.transition.slide_bottom);
        pastEventWaitFragment = PastEventWaitFragment.newInstance(false, images, position);
        // Setup exit transition on first fragment
        pastEventDetailFragment.setSharedElementReturnTransition(changeTransform);
        pastEventDetailFragment.setExitTransition(explodeTransform);
        binding.abEventDetail.setExpanded(false, true);
        // Setup enter transition on second fragment
        pastEventWaitFragment.setSharedElementEnterTransition(changeTransform);
        pastEventWaitFragment.setEnterTransition(slideBottom);

        // Find the shared element (in Fragment A)
        ImageView ivGalleryImage = (ImageView) findViewById(R.id.ivEDImage);

        // Add second fragment by replacing first
        FragmentTransaction ft = fragmentManager.beginTransaction()
                .replace(R.id.flMessages, pastEventWaitFragment)
                .addToBackStack("transaction")
                .addSharedElement(ivGalleryImage, "galleryImage");
        // Apply the transaction
        ft.commit();
    }

    @Override
    public void onTextChange(int count) {
        Log.d(TAG, "Count: "+count);
        if (count == 0) {
            binding.famEDMedia.setVisibility(View.VISIBLE);
            binding.fabEDSend.setVisibility(View.GONE);
        } else {
            binding.famEDMedia.setVisibility(View.GONE);
            binding.fabEDSend.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onMessageCreated(Message message) {
        upcomingEventDetailFragment.updateMessage(message);
    }

    @Override
    public void onFocused() {
        upcomingEventDetailFragment.updateScroll();
    }
}
