package com.codepath.insync.activities;

import android.animation.Animator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.codepath.insync.R;
import com.codepath.insync.adapters.CustomMapWindowAdapter;
import com.codepath.insync.adapters.LTImageAdapter;
import com.codepath.insync.databinding.ActivityLocationTrackerBinding;
import com.codepath.insync.models.parse.Event;
import com.codepath.insync.models.parse.User;
import com.codepath.insync.models.parse.UserEventRelation;
import com.codepath.insync.utils.CommonUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LocationTrackerActivity extends AppCompatActivity {

    private static final String TAG = "LocationTrackerActivity";
    private static final int ZOOM_FACTOR = 10;
    private GoogleMap map;
    LatLng eventLocation;
    String eventId;
    Event event;
    View mapsContainer;
    ActivityLocationTrackerBinding binding;
    Map<String, Marker> userMap;
    boolean mfirstLoad;
    BroadcastReceiver messageReceiver;
    List<User> ltUsers;
    LTImageAdapter ltImageAdapter;
    LinearLayoutManager linearLayoutManager;
    private boolean shouldUpdateLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.do_not_move, R.anim.do_not_move);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_location_tracker);
        mapsContainer = findViewById(R.id.mapContainer);
        userMap = new HashMap<>();
        mfirstLoad = true;
        shouldUpdateLocation = false;
        processIntent();
        setupRecyclerView();

        mapsContainer.setVisibility(View.INVISIBLE);
        if (TextUtils.isEmpty(getResources().getString(R.string.google_maps_api_key))) {
            throw new IllegalStateException("You forgot to supply a Google Maps API key");
        }

        SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    loadMap(map);
                    map.setInfoWindowAdapter(new CustomMapWindowAdapter(getLayoutInflater()));
                }
            });
        } else {
            CommonUtil.createSnackbar(mapsContainer, this, "Could not locate guests at this time. Please try later.");
            finish();
        }

    }

    @Override
    public void onPause() {
        unregisterReceiver(messageReceiver);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("com.codepath.insync.Users");
        messageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Broadcast received. Should update location: "+shouldUpdateLocation);
                if (shouldUpdateLocation) {
                    shouldUpdateLocation = false;
                    updateUsersLocations();
                }

            }
        };
        registerReceiver(messageReceiver, filter);
    }

    private void processIntent() {
        eventId = getIntent().getStringExtra("eventId");
        eventLocation = new LatLng(
                getIntent().getDoubleExtra("eventLatitude", 0.0),
                getIntent().getDoubleExtra("eventLongitude", 0.0));
        event = new Event();
        event.setObjectId(eventId);

        updateUsersLocations();
    }

    private void setupRecyclerView() {
        ltUsers = new ArrayList<>();
        ltImageAdapter = new LTImageAdapter(this, ltUsers, R.layout.item_ltimage);
        binding.rvLTImages.setAdapter(ltImageAdapter);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        binding.rvLTImages.setLayoutManager(linearLayoutManager);
        ltImageAdapter.setOnItemClickListener(new LTImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                User user = ltUsers.get(position);
                ParseGeoPoint userLocation = user.getLocation();
                if (userLocation == null) {
                    return;
                }
                LatLng currLocation = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());
                setLocation(currLocation);
            }
        });
    }

    private void updateUsersLocations() {
        ParseQuery<UserEventRelation> userEventQuery = ParseQuery.getQuery(UserEventRelation.class);
        userEventQuery.whereEqualTo("event", event);

        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.whereMatchesKeyInQuery("objectId", "userId", userEventQuery);

        userQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Found the following users: "+parseUsers.size());
                    for (ParseUser parseUser : parseUsers) {
                        final User user = new User(parseUser);
                        ParseGeoPoint userLocation = user.getLocation();
                        if (userLocation == null) {
                            continue;
                        }
                        final LatLng currLocation = new LatLng(userLocation.getLatitude(), userLocation.getLongitude());

                        if (userMap.containsKey(user.getObjectId())) {
                            Marker userMarker = userMap.get(user.getObjectId());
                            animateMarker(userMarker, currLocation, false);
                        } else {
                            String imageUrl = null;
                            if (user.getProfileImage() != null) {
                                imageUrl = user.getProfileImage().getUrl();
                            }
                            if (ltUsers.size() < parseUsers.size()) {
                                ltUsers.add(user);
                                ltImageAdapter.notifyItemInserted(ltUsers.size()-1);
                            }

                            if (imageUrl != null) {
                                Glide.with(getApplicationContext())
                                        .load(imageUrl)
                                        .asBitmap()
                                        .placeholder(R.drawable.ic_profile)
                                        .into(new SimpleTarget<Bitmap>() {

                                            @Override
                                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                                Marker userMarker = map.addMarker(new MarkerOptions()
                                                        .title(user.getName())
                                                        .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmap(resource)))
                                                        .position(currLocation)
                                                        .anchor(0.5f, 1));
                                                userMap.put(user.getObjectId(), userMarker);
                                                animateMarker(userMarker, currLocation, false);
                                            }
                                        });
                            } else {
                                Marker userMarker = map.addMarker(new MarkerOptions()
                                        .title(user.getName())
                                        .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmap(null)))
                                        .position(currLocation)
                                        .anchor(0.5f, 1));
                                userMap.put(user.getObjectId(), userMarker);
                                animateMarker(userMarker, currLocation, false);
                            }
                        }
                    }
                    ParseGeoPoint currUserGeo = User.getCurrentUser().getParseGeoPoint("location");
                    if (currUserGeo != null) {
                        LatLng currUserLoc = new LatLng(currUserGeo.getLatitude(), currUserGeo.getLongitude());
                        if (mfirstLoad) {
                            setLocation(currUserLoc);
                            float finalRadius = Math.max(mapsContainer.getWidth(), mapsContainer.getHeight()) * 2.0f;

                            circularRevealActivity(0, finalRadius, false);
                            mfirstLoad = false;
                        }
                    }
                } else {
                    CommonUtil.createSnackbar(
                            mapsContainer, getApplicationContext(), "Could not locate guests at this time. Please try later.");

                    finish();
                }
                shouldUpdateLocation = true;
            }
        });

    }

    private void circularRevealActivity(float initRadius, float finalRadius, final boolean doFinish) {

        int cx = mapsContainer.getLeft();
        int cy = mapsContainer.getTop();


        // create the animator for this view (the start radius is zero)
        Animator circularReveal = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            circularReveal = ViewAnimationUtils.createCircularReveal(mapsContainer, cx, cy, initRadius, finalRadius);
        }

        // make the view visible and start the animation
        mapsContainer.setVisibility(View.VISIBLE);
        if (circularReveal == null) {
            return;
        }

        circularReveal.setDuration(2000);
        circularReveal.start();


        circularReveal.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (doFinish) {
                    exitLocationTracker();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }

    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map == null) {
            // Map is null
            CommonUtil.createSnackbar(mapsContainer, this, "Could not locate guests at this time. Please try later.");
            finish();
        }
    }

    private void setLocation(LatLng latLng) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_FACTOR);
        //map.animateCamera(cameraUpdate);
        map.moveCamera(cameraUpdate);
    }

    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = map.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 15ms later.
                    handler.postDelayed(this, 15);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

    private Bitmap getMarkerBitmap(Bitmap bitmap) {
        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.layout_custom_map_marker, binding.rlLocationTracker, false);
        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.iv_custom_marker);
        if (bitmap == null) {
            markerImageView.setImageResource(R.drawable.ic_profile);
        } else {
            markerImageView.setImageBitmap(bitmap);
        }

        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);

        return returnedBitmap;
    }

    public void onHomeClick(View view) {
        float finalRadius = Math.max(mapsContainer.getWidth(), mapsContainer.getHeight()) * 2.0f;
        circularRevealActivity(finalRadius, 0, true);
    }

    @Override
    public void onBackPressed() {
        float finalRadius = Math.max(mapsContainer.getWidth(), mapsContainer.getHeight()) * 2.0f;
        circularRevealActivity(finalRadius, 0, true);
    }

    private void exitLocationTracker() {
        mapsContainer.setVisibility(View.INVISIBLE);
        finish();
        overridePendingTransition(R.anim.do_not_move, R.anim.do_not_move);
    }
}
