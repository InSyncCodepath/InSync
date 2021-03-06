package com.codepath.insync.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;


import com.codepath.insync.R;
import com.codepath.insync.adapters.EDGuestAdapter;
import com.codepath.insync.databinding.ActivityEventDetailMoreBinding;
import com.codepath.insync.models.parse.Event;
import com.codepath.insync.models.parse.User;
import com.codepath.insync.models.parse.UserEventRelation;
import com.codepath.insync.utils.Constants;
import com.codepath.insync.utils.CommonUtil;
import com.codepath.insync.widgets.CustomLineItemDecoration;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;

import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;


public class EventDetailMoreActivity extends AppCompatActivity
{

    private static final String TAG = "EventDetailMoreActivity";


    ActivityEventDetailMoreBinding binding;
    CollapsingToolbarLayout collapsingToolbar;
    Event event;
    String eventId;
    boolean canTrack;
    int numAttending;
    int numDecline;
    int numPending;
    int currentRbnId;
    UserEventRelation currentUserEvent;
    private boolean firstLoad;
    private boolean firstGuestLoad;
    FragmentManager fragmentManager;
    List<User> guests;
    EDGuestAdapter guestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_event_detail_more);

        fragmentManager = getSupportFragmentManager();
        firstLoad = true;
        firstGuestLoad = true;
        guests = new ArrayList<>();

        processIntent();
        setupToolbar();
        setupClickListeners();
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        guestAdapter = new EDGuestAdapter(this, guests, R.layout.item_edguest, true);
        binding.rvEDGuests.setAdapter(guestAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.rvEDGuests.setLayoutManager(linearLayoutManager);
        binding.rvEDGuests.addItemDecoration(new CustomLineItemDecoration(this));
        binding.rvEDGuests.setNestedScrollingEnabled(false);

    }
    private void setupClickListeners() {
        binding.tvEDLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseGeoPoint eventLoc = event.getLocation();
                String navString = "geo:"+eventLoc.getLatitude()+","+eventLoc.getLongitude()+"?z=10&q="+event.getAddress();
                Log.d(TAG, navString);
                Uri gmmIntentUri = Uri.parse(navString);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    CommonUtil.createSnackbar(binding.clED, getApplicationContext(), "Cannot open maps at this time. Please try later");
                }
            }
        });
    }



    private void processIntent() {
        Intent intent = getIntent();
        eventId = intent.getStringExtra("eventId");
        canTrack = intent.getBooleanExtra("canTrack", false);
        binding.ivEDProfile.setTransitionName(intent.getStringExtra("transition_name"));
        Event.findEvent(eventId, new GetCallback<Event>() {
            @Override
            public void done(Event eventObj, ParseException e) {
                if (e == null) {
                    event = eventObj;
                    findAttendance();
                    loadViews();
                    //loadFragments();
                } else {
                    event = null;
                    Log.e(TAG, "Error finding event.");
                    finish();
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_right);
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_right);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void findAttendance() {
        numAttending = 0;
        numDecline = 0;
        numPending = 0;
        guests.add(0, User.getCurrentUser());
        guestAdapter.notifyItemInserted(0);
        UserEventRelation.findAttendees(event, new FindCallback<UserEventRelation>() {
            @Override
            public void done(List<UserEventRelation> userEventRelations, ParseException e) {
                if (e == null) {
                    for (UserEventRelation userEventRelation : userEventRelations) {
                        final int rsvpStatus = userEventRelation.getRsvpStatus();
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
                        if (firstGuestLoad) {
                            User.findUser(userEventRelation.getUserId(), new GetCallback<ParseUser>() {
                                @Override
                                public void done(ParseUser parseUser, ParseException e) {
                                    User user = new User(parseUser);
                                    user.put("rsvpStatus", rsvpStatus);

                                    if (!user.getObjectId().equals(User.getCurrentUser().getObjectId())) {
                                        guests.add(user);
                                        guestAdapter.notifyItemInserted(guests.size()-1);
                                    }
                                }

                            });
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
                    firstGuestLoad = false;
                } else {
                    Log.e(TAG, "Error loading RSVP status: "+e.getLocalizedMessage());
                }
                binding.rbnAttending.setText(numAttending+" "+Constants.ATTENDING_STR);
                binding.rbnDecline.setText(numDecline+" "+Constants.DECLINE_STR);
                binding.rbnPending.setText(numPending+" "+Constants.PENDING_STR);
            }
        });
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
                    .resize(binding.ivEDProfile.getWidth(), 0)
                    .transform(new RoundedCornersTransformation(10, 0)).into(binding.ivEDProfile,
                    new Callback() {
                        @Override
                        public void onSuccess() {
                            // Call the "scheduleStartPostponedTransition()" method
                            // below when you know for certain that the shared element is
                            // ready for the transition to begin.
                            binding.abEventDetail.post(new Runnable() {
                                @Override
                                public void run() {
                                    int offsetPx = binding.abEventDetail.getHeight() - (3*binding.tbEventDetail.getHeight());
                                    Log.d(TAG, "offset for appbar: "+offsetPx+", "+binding.abEventDetail.getMeasuredHeight()+", "+binding.tbEventDetail.getHeight());
                                    CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) binding.abEventDetail.getLayoutParams();
                                    AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
                                    behavior.onNestedPreScroll(binding.clED, binding.abEventDetail, null, 0, offsetPx, new int[]{0, 0});
                                }
                            });
                        }

                        @Override
                        public void onError() {
                            // ...
                        }
                    });

        }
        binding.tvEDName.setText(event.getName());
        binding.tvEDDescription.setText(event.getDescription());
        binding.tvEDStartDate.setText(CommonUtil.getDateTimeInFormat(event.getStartDate()));
        binding.tvEDEndDate.setText(CommonUtil.getDateTimeInFormat(event.getEndDate()));
        binding.tvEDLocation.setText(event.getAddress());
    }



    private void setupToolbar() {

        collapsingToolbar = binding.ctlEventDetail;

        setSupportActionBar(binding.tbEventDetail);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
            actionBar.setDisplayHomeAsUpEnabled(true);

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
            collapsingToolbar.setContentScrimColor(getColor(R.color.primary));
            collapsingToolbar.setStatusBarScrimColor(getColor(R.color.accent));
        }


        binding.abEventDetail.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                Log.d(TAG, "Appbar offset changed to: "+verticalOffset+" , appbar height: "+appBarLayout.getMeasuredHeight()+", toolbar height: "+binding.tbEventDetail.getHeight());
                // Calculate ActionBar height

                if (Math.abs(verticalOffset) + binding.tbEventDetail.getHeight() + 94 >= appBarLayout.getMeasuredHeight()) {
                    collapsingToolbar.setTitle(binding.tvEDName.getText());
                    collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(getApplicationContext(), R.color.primary_text_white));
                    collapsingToolbar.setCollapsedTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.primary_text_white));
                    binding.ivEDProfile.setVisibility(View.INVISIBLE);
                    binding.tvEDName.setVisibility(View.INVISIBLE);
                    Drawable toolbarDrawable = ResourcesCompat.getDrawable(getResources(),
                            R.drawable.theme_gradient, null);
                    collapsingToolbar.setBackground(toolbarDrawable);
                } else {
                    collapsingToolbar.setTitle(Constants.EMPTY_STR);
                    binding.ivEDProfile.setVisibility(View.VISIBLE);
                    binding.tvEDName.setVisibility(View.VISIBLE);
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
                    guests.get(0).put("rsvpStatus", Constants.ATTENDING);
                    numAttending++;
                } else if (checkedId == binding.rbnDecline.getId()) {
                    currentUserEvent.setRsvpStatusKey(Constants.DECLINE);
                    guests.get(0).put("rsvpStatus", Constants.DECLINE);
                    numDecline++;
                } else {
                    currentUserEvent.setRsvpStatusKey(Constants.PENDING);
                    guests.get(0).put("rsvpStatus", Constants.PENDING);
                    numPending++;
                }

                guestAdapter.notifyItemChanged(0);
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

    /*private void loadFragments() {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if (isCurrent) {
            // Load current and upcoming event detail
            upcomingEventDetailFragment = UpcomingEventDetailFragment.newInstance(eventId);
            ft.replace(R.id.flMessages, upcomingEventDetailFragment);
            messageSendFragment = MessageSendFragment.newInstance(eventId);
            ft.replace(R.id.flMessageSend, messageSendFragment);
            setupUI(binding.clED);
        } else {
            pastEventDetailFragment =
                    PastEventDetailFragment.newInstance(event.getObjectId(), event.getName(), event.getTheme());
            ft.replace(R.id.flMessages, pastEventDetailFragment);
        }

        ft.commit();

    }*/




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_right);
    }

}